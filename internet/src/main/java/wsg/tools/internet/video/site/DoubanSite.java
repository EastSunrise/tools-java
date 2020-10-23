package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.exception.LoginException;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.base.exception.UnexpectedContentException;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;
import wsg.tools.internet.video.entity.douban.base.BaseSuggestItem;
import wsg.tools.internet.video.entity.douban.base.LoginResult;
import wsg.tools.internet.video.entity.douban.object.DoubanMovie;
import wsg.tools.internet.video.entity.douban.object.DoubanSeries;
import wsg.tools.internet.video.entity.douban.object.SearchItem;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.MarkEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URLDecoder;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Obtains info from <a href="https://douban.com">豆瓣</a>.
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
public class DoubanSite extends BaseSite {

    public static final LocalDate DOUBAN_START_DATE = LocalDate.of(2005, 3, 6);
    public static final String TITLE_PERMIT_CHARS = "[ ?#&*,-.!'0-9:;A-z·\u0080-\u024F\u0400-\u04FF\u0600-\u06FF\u0E00-\u0E7F\u2150-\u218F\u3040-\u30FF\u4E00-\u9FBF\uAC00-\uD7AF！：。]";
    public static final Pattern URL_MOVIE_SUBJECT_REGEX = Pattern.compile("https://movie.douban.com/subject/(?<id>\\d{7,8})/?");
    protected static final int MAX_COUNT_ONCE = 100;
    protected static final int COUNT_PER_PAGE = 15;
    private static final Pattern CREATORS_PAGE_TITLE_REGEX = Pattern.compile("[^()\\s]+\\((\\d+)\\)");
    private static final Pattern PAGE_TITLE_REGEX = Pattern.compile("(.*)\\s\\(豆瓣\\)");
    private static final Pattern COLLECTIONS_PAGE_REGEX = Pattern.compile("(\\d+)-(\\d+)\\s/\\s(\\d+)");
    private static final Pattern EXT_DURATIONS_REGEX = Pattern.compile("(\\d+) ?分钟");
    private static final Pattern COOKIE_DBCL2_REGEX = Pattern.compile("\"(?<id>\\d+):[0-9+/A-z]+\"");
    private static final Pattern SEARCH_ITEM_HREF_REGEX =
            Pattern.compile("https://www\\.douban\\.com/link2/\\?url=(?<url>[0-9A-z%.-]+)&query=(?<q>[0-9A-z%]+)&cat_id=(?<cat>\\d*)&type=search&pos=(?<pos>\\d+)");

    public DoubanSite() {
        super("Douban", "douban.com", 1);
    }

    @Override
    protected ObjectMapper objectMapper() {
        return super.objectMapper()
                .registerModule(new SimpleModule()
                        .addDeserializer(GenreEnum.class, EnumDeserializers.getTitleDeserializer(GenreEnum.class))
                ).registerModule(new JavaTimeModule()
                        .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(Constants.STANDARD_DATE_TIME_FORMATTER))
                );
    }

    public final void login(String username, String password) {
        logout();
        if (getCookies().size() == 0) {
            try {
                getDocument(builder0(null), false);
            } catch (NotFoundException e) {
                throw AssertUtils.runtimeException(e);
            }
        }
        LoginResult loginResult;
        try {
            loginResult = postObject(builder("accounts", "/j/mobile/login/basic"), Arrays.asList(
                    new BasicNameValuePair("ck", ""),
                    new BasicNameValuePair("name", username),
                    new BasicNameValuePair("password", password),
                    new BasicNameValuePair("remember", String.valueOf(true))
            ), LoginResult.class, false);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        if (!loginResult.isSuccess()) {
            throw new LoginException(loginResult.getMessage());
        }
        updateContext();
    }

    @Override
    public final String user() {
        Cookie cookie = getCookie("dbcl2");
        if (cookie == null) {
            return null;
        }
        return AssertUtils.matches(COOKIE_DBCL2_REGEX, cookie.getValue()).group("id");
    }

    private String ck() {
        Cookie cookie = getCookie("ck");
        if (cookie == null) {
            return null;
        }
        return cookie.getValue();
    }

    /**
     * Parse the page of a subject to get its info.
     * <p>
     * This method can't obtain x-rated subject probably.
     *
     * @return IMDb id
     */
    public BaseDoubanSubject subject(long subjectId) throws NotFoundException {
        Document document = getDocument(builder(CatalogEnum.MOVIE.getPath(), "/subject/%d", subjectId), true);
        String text = document.selectFirst("script[type=application/ld+json]").html();
        text = StringUtils.replaceChars(text, "\n\t", "");
        BaseDoubanSubject subject;
        try {
            subject = mapper.readValue(text, BaseDoubanSubject.class);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }

        String title = AssertUtils.matches(PAGE_TITLE_REGEX, document.title()).group(1);
        subject.setTitle(title);
        String name = subject.getName().replace("  ", " ");
        if (name.startsWith(title)) {
            if (name.length() > title.length()) {
                subject.setOriginalTitle(StringEscapeUtils.unescapeHtml4(name.substring(title.length()).strip()));
            }
        } else {
            throw new UnexpectedContentException("Name and title are not matched.");
        }

        String year = StringUtils.strip(document.selectFirst("span.year").html(), "()");
        subject.setYear(Year.of(Integer.parseInt(year)));

        Element info = document.selectFirst("div#info");
        Map<String, Element> spans = info.select("span.pl").stream().collect(Collectors.toMap(Element::text, span -> span));
        Element span;

        final String plLanguage = "语言:";
        if ((span = spans.get(plLanguage)) != null) {
            String[] languages = StringUtils.split(((TextNode) span.nextSibling()).text(), SignEnum.SLASH.getC());
            subject.setLanguages(Arrays.stream(languages).map(
                    language -> EnumUtilExt.deserializeAka(language.strip(), LanguageEnum.class)
            ).collect(Collectors.toList()));
        }
        final String plImdb = "IMDb链接:";
        if ((span = spans.get(plImdb)) != null) {
            subject.setImdbId(span.nextElementSibling().text().strip());
        }

        final String propertyRuntime = "span[property=v:runtime]";
        if ((span = info.selectFirst(propertyRuntime)) != null && subject instanceof DoubanMovie) {
            Node node = span.nextSibling();
            if (node instanceof TextNode && !((TextNode) node).isBlank()) {
                ((DoubanMovie) subject).setExtDurations(new ArrayList<>());
                Matcher matcher = EXT_DURATIONS_REGEX.matcher(((TextNode) node).text().strip());
                while (matcher.find()) {
                    ((DoubanMovie) subject).getExtDurations().add(Duration.ofMinutes(Long.parseLong(matcher.group(1))));
                }
            }
        }

        if (subject instanceof DoubanSeries) {
            DoubanSeries series = (DoubanSeries) subject;
            final String plEpisodes = "集数:";
            if ((span = spans.get(plEpisodes)) != null) {
                series.setEpisodesCount(Integer.parseInt(((TextNode) span.nextSibling()).text().strip()));
            }
        }

        return subject;
    }

    /**
     * Obtains id of Douban by searching id of IMDb.
     */
    @Nullable
    public Long getDbIdByImdbId(String imdbId) {
        if (user() == null) {
            throw new LoginException("Please log in first.");
        }
        AssertUtils.requireNotBlank(imdbId);
        Document document;
        try {
            CatalogEnum cat = CatalogEnum.MOVIE;
            document = postDocument(builder(cat.getPath(), "/new_subject"), Arrays.asList(
                    new BasicNameValuePair("ck", ck()),
                    new BasicNameValuePair("type", "0"),
                    new BasicNameValuePair("p_title", imdbId),
                    new BasicNameValuePair("p_uid", imdbId),
                    new BasicNameValuePair("cat", String.valueOf(cat.getCode())),
                    new BasicNameValuePair("subject_submit", "下一步")
            ), true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }

        Element fieldset = document.selectFirst("div#content").selectFirst(TAG_FIELDSET);
        Element input = fieldset.selectFirst("input#p_uid");
        if (input == null) {
            return null;
        }
        String href = input.nextElementSibling().nextElementSibling().attr(ATTR_HREF);
        return Long.parseLong(AssertUtils.matches(URL_MOVIE_SUBJECT_REGEX, href).group("id"));
    }

    /**
     * Search items by specified keyword under sub module.
     *
     * @param catalog which catalog
     * @param keyword not blank
     */
    public List<SearchItem> search(@Nonnull CatalogEnum catalog, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            throw new IllegalArgumentException("Keyword mustn't be blank.");
        }
        URIBuilder builder = builder("search", "/%s/subject_search", catalog.getPath())
                .setParameter("search_text", keyword)
                .setParameter("cat", String.valueOf(catalog.getCode()));
        Document document;
        try {
            document = getDocument(builder, true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }

        return document.select("div.item-root").stream()
                .map(div -> {
                    Element a = div.selectFirst("a.title-text");
                    SearchItem item = new SearchItem();
                    item.setTitle(a.text().strip());
                    item.setUrl(a.attr(ATTR_HREF));
                    return item;
                }).collect(Collectors.toList());
    }

    /**
     * Search items by specified keyword.
     *
     * @param catalog which catalog
     * @param keyword not blank
     */
    public List<SearchItem> search0(@Nullable CatalogEnum catalog, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            throw new IllegalArgumentException("Keyword mustn't be blank.");
        }
        URIBuilder builder = builder0("/search")
                .setParameter("q", keyword);
        if (catalog != null) {
            builder.setParameter("cat", String.valueOf(catalog.getCode()));
        }
        Document document;
        try {
            document = getDocument(builder, true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }

        return document.selectFirst("div.search-result").select("div.result").stream()
                .map(div -> {
                    Element a = div.selectFirst(TAG_H3).selectFirst(TAG_A);
                    SearchItem item = new SearchItem();
                    Matcher matcher = AssertUtils.matches(SEARCH_ITEM_HREF_REGEX, a.attr(ATTR_HREF));
                    item.setTitle(a.text().strip());
                    item.setUrl(URLDecoder.decode(matcher.group("url"), Constants.UTF_8));
                    return item;
                }).collect(Collectors.toList());
    }

    /**
     * Suggested by keyword.
     * <p>
     * Attention: items are not returned every time since non-null response may have delay.
     *
     * @param catalog which catalog
     * @param keyword not blank
     */
    public List<BaseSuggestItem> suggest(CatalogEnum catalog, String keyword) {
        if (StringUtils.isBlank(keyword)) {
            throw new IllegalArgumentException("Keyword mustn't be blank.");
        }
        URIBuilder builder = builder(catalog.getPath(), "/j/subject_suggest")
                .setParameter("q", keyword);
        try {
            return getObject(builder, new TypeReference<>() {}, true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * Returns marked subjects of the given user since the given start date.
     *
     * @param catalog movie/book/music/...
     * @param mark    wish/do/collect
     * @return map of (id, mark date)
     */
    public Map<Long, LocalDate> collectUserSubjects(long userId, LocalDate since, CatalogEnum catalog, MarkEnum mark)
            throws NotFoundException {
        if (since == null) {
            since = DOUBAN_START_DATE;
        }
        log.info("Collect {} of user {} since {}", catalog, userId, since);
        Map<Long, LocalDate> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        int start = 0;
        while (true) {
            URIBuilder builder = builder(catalog.getPath(), "/people/%d/%s", userId, mark.getPath())
                    .addParameter("sort", "time")
                    .addParameter("start", String.valueOf(start))
                    .addParameter("mode", "list");
            Document document = getDocument(builder, false);
            boolean done = false;
            String listClass = ".list-view";
            for (Element li : document.selectFirst(listClass).select(TAG_LI)) {
                Element div = li.selectFirst(".title");
                String href = div.selectFirst(TAG_A).attr("href");
                long id = Long.parseLong(StringUtils.substringAfterLast(StringUtils.strip(href, "/"), "/"));
                LocalDate markDate = LocalDate.parse(div.nextElementSibling().text().strip());
                if (!markDate.isBefore(since)) {
                    map.put(id, markDate);
                    start++;
                } else {
                    done = true;
                    break;
                }
            }

            String numStr = document.selectFirst("span.subject-num").text().strip();
            Matcher matcher = AssertUtils.matches(COLLECTIONS_PAGE_REGEX, numStr);
            if (start >= Integer.parseInt(matcher.group(3)) || done) {
                break;
            }
        }
        log.info("Collected {} {}", map.size(), catalog);
        return map;
    }

    /**
     * Returns ids of collected creators.
     *
     * @param catalog movie/book/music/...
     */
    public List<Long> collectUserCreators(long userId, CatalogEnum catalog) throws NotFoundException {
        log.info("Collect {} of user {}", catalog.getCreator().getPath(), userId);
        List<Long> ids = new LinkedList<>();
        int start = 0;
        while (true) {
            URIBuilder builder = builder(catalog.getPath(), "/people/%d/%s", userId, catalog.getCreator().getPath())
                    .addParameter("start", String.valueOf(start));
            Document document = getDocument(builder, false);
            String itemClass = ".item";
            for (Element div : document.select(itemClass)) {
                Element a = div.selectFirst(".title").selectFirst(TAG_A);
                String href = a.attr("href");
                ids.add(Long.parseLong(StringUtils.substringAfterLast(StringUtils.strip(href, "/"), "/")));
                start++;
            }
            Matcher matcher = AssertUtils.matches(CREATORS_PAGE_TITLE_REGEX, document.title().strip());
            if (start >= Integer.parseInt(matcher.group(1))) {
                break;
            }
        }
        log.info("Collected {} {}", ids.size(), catalog.getCreator().getPath());
        return ids;
    }
}
