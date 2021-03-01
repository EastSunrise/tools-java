package wsg.tools.internet.video.site.douban;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.RepositoryImpl;
import wsg.tools.internet.base.RequestBuilder;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.common.Loggable;
import wsg.tools.internet.common.LoginException;
import wsg.tools.internet.common.UnexpectedContentException;
import wsg.tools.internet.video.common.Parsers;
import wsg.tools.internet.video.common.Runtime;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.MarkEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class DoubanSite extends RepositoryImpl implements Loggable<Integer> {

    public static final LocalDate DOUBAN_START_DATE = LocalDate.of(2005, 3, 6);
    public static final Pattern URL_MOVIE_SUBJECT_REGEX = Pattern.compile("https://movie.douban.com/subject/(?<id>\\d{7,8})/?");

    protected static final int MAX_COUNT_ONCE = 100;
    private static final Pattern CREATORS_PAGE_TITLE_REGEX = Pattern.compile("[^()\\s]+\\((\\d+)\\)");
    private static final Pattern PAGE_TITLE_REGEX = Pattern.compile("(?<t>.*)\\s\\(豆瓣\\)");
    private static final Pattern COLLECTIONS_PAGE_REGEX = Pattern.compile("(\\d+)-(\\d+)\\s/\\s(\\d+)");
    private static final Pattern COOKIE_DBCL2_REGEX = Pattern.compile("\"(?<id>\\d+):[0-9A-Za-z+/]+\"");
    private static final Pattern SEARCH_ITEM_HREF_REGEX =
            Pattern.compile("https://www\\.douban\\.com/link2/\\?url=(?<url>[0-9A-Za-z%.-]+)&query=(?<q>[0-9A-Za-z%]+)&cat_id=(?<cat>\\d*)&type=search&pos=(?<pos>\\d+)");

    protected static ObjectMapper mapper = new ObjectMapper()
            .registerModule(new SimpleModule()
                    .addDeserializer(GenreEnum.class, EnumDeserializers.getTitleDeserializer(GenreEnum.class))
            ).registerModule(new JavaTimeModule()
                    .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(Constants.STANDARD_DATE_TIME_FORMATTER))
            );
    private static DoubanSite instance;

    protected DoubanSite() {
        super("Douban", "douban.com");
    }

    public static DoubanSite getInstance() {
        if (instance == null) {
            instance = new DoubanSite();
        }
        return instance;
    }

    /**
     * Log in the site with the given username and password.
     *
     * @throws LoginException if the given user and password is invalid or a CAPTCHA is required.
     */
    public final void login(String username, String password) throws LoginException, HttpResponseException {
        logout();
        getDocument(builder0(null), SnapshotStrategy.ALWAYS_UPDATE);
        RequestBuilder builder = create(HttpPost.METHOD_NAME, "accounts", "/j/mobile/login/basic")
                .addParameter("ck", "")
                .addParameter("name", username)
                .addParameter("password", password)
                .addParameter("remember", String.valueOf(true));
        LoginResult loginResult = getObject(builder, mapper, LoginResult.class, SnapshotStrategy.ALWAYS_UPDATE);
        if (!loginResult.isSuccess()) {
            throw new LoginException(loginResult.getMessage());
        }
    }

    @Override
    public final Integer user() {
        Cookie cookie = getCookie("dbcl2");
        if (cookie == null) {
            return null;
        }
        return Integer.parseInt(RegexUtils.matchesOrElseThrow(COOKIE_DBCL2_REGEX, cookie.getValue()).group("id"));
    }

    public final void logout() throws HttpResponseException {
        if (user() == null) {
            return;
        }
        getDocument(builder0(null), SnapshotStrategy.ALWAYS_UPDATE);
        RequestBuilder builder = builder0("/accounts/logout")
                .addParameter("source", "main")
                .addParameter("ck", Objects.requireNonNull(getCookie("ck")).getValue());
        getDocument(builder, SnapshotStrategy.ALWAYS_UPDATE);
    }

    /**
     * Obtains a subject by the given identifier.
     * <p>
     * Some x-rated subjects may be restricted to access without logging in.
     */
    public final BaseDoubanSubject subject(long subjectId) throws HttpResponseException {
        Document document = getDocument(builder(CatalogEnum.MOVIE.getPath(), "/subject/%d", subjectId), SnapshotStrategy.NEVER_UPDATE);
        String text = document.selectFirst("script[type=application/ld+json]").html();
        text = StringUtils.replaceChars(text, "\n\t", "");
        BaseDoubanSubject subject;
        try {
            subject = mapper.readValue(text, BaseDoubanSubject.class);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }

        subject.setId(subjectId);
        String zhTitle = RegexUtils.matchesOrElseThrow(PAGE_TITLE_REGEX, document.title()).group("t");
        subject.setZhTitle(zhTitle);
        String name = subject.getName().replace("  ", " ");
        if (name.startsWith(zhTitle)) {
            if (name.length() > zhTitle.length()) {
                subject.setOriginalTitle(StringEscapeUtils.unescapeHtml4(name.substring(zhTitle.length()).strip()));
            }
        } else {
            throw new UnexpectedContentException("Name and zhTitle are not matched.");
        }

        String year = StringUtils.strip(document.selectFirst("span.year").html(), "()");
        subject.setYear(Integer.parseInt(year));

        Element rating = document.selectFirst("div.rating_right");
        subject.setReleased(!rating.hasClass("not_showed"));

        if (subject instanceof DoubanSeries) {
            Element season = document.selectFirst("#season");
            if (season != null) {
                Elements options = season.select(CssSelector.TAG_OPTION);
                long[] seasons = new long[options.size()];
                for (Element option : options) {
                    seasons[Integer.parseInt(option.text()) - 1] = Long.parseLong(option.val());
                    if (option.hasAttr("selected")) {
                        ((DoubanSeries) subject).setCurrentSeason(Integer.parseInt(option.text()));
                    }
                }
                ((DoubanSeries) subject).setSeasons(seasons);
            }
        }
        Element info = document.selectFirst("div#info");
        extractInfo(subject, info.select("span.pl").stream().collect(Collectors.toMap(Element::text, e -> e)));
        return subject;
    }

    private void extractInfo(BaseDoubanSubject subject, Map<String, Element> spans) {
        Element span;
        final String plLanguage = "语言:";
        if ((span = spans.get(plLanguage)) != null) {
            String[] languages = StringUtils.split(((TextNode) span.nextSibling()).text(), "/");
            subject.setLanguages(Arrays.stream(languages).map(
                    language -> EnumUtilExt.deserializeAka(language.strip(), LanguageEnum.class)
            ).collect(Collectors.toList()));
        }
        final String plImdb = "IMDb链接:";
        if ((span = spans.get(plImdb)) != null) {
            subject.setImdbId(span.nextElementSibling().text().strip());
        }

        if (subject instanceof DoubanMovie) {
            DoubanMovie movie = (DoubanMovie) subject;
            final String plDuration = "片长:";
            movie.setRuntimes(getRuntimes(spans.get(plDuration)));
        }

        if (subject instanceof DoubanSeries) {
            DoubanSeries series = (DoubanSeries) subject;
            final String plEpisodes = "集数:";
            if ((span = spans.get(plEpisodes)) != null) {
                series.setEpisodesCount(Integer.parseInt(((TextNode) span.nextSibling()).text().strip()));
            }
            final String plDuration = "单集片长:";
            series.setRuntimes(getRuntimes(spans.get(plDuration)));
        }
    }

    private List<Runtime> getRuntimes(Element span) {
        if (span != null) {
            Element element = span.nextElementSibling();
            Node node = element.is(CssSelector.TAG_SPAN) ? element.nextSibling() : element.previousSibling();
            if (node instanceof TextNode) {
                String[] parts = StringUtils.strip(((TextNode) node).text(), " /").split("/");
                return Arrays.stream(parts).map(String::strip).map(Runtime::of).collect(Collectors.toList());
            }
        }
        return null;
    }

    /**
     * Obtains marked subjects of the given user since the given start date.
     *
     * @param userId  id of the user which returned by {@link #user()}
     * @param catalog movie/book/music/...
     * @param mark    wish/do/collect
     * @return map of (id, mark date)
     */
    public final Map<Long, LocalDate> collectUserSubjects(long userId, LocalDate since, CatalogEnum catalog, MarkEnum mark) throws HttpResponseException {
        if (since == null) {
            since = DOUBAN_START_DATE;
        }
        log.info("Collect {} {} of user {} since {}", mark, catalog, userId, since);
        Map<Long, LocalDate> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        int start = 0;
        while (true) {
            RequestBuilder builder = builder(catalog.getPath(), "/people/%d/%s", userId, mark.getPath())
                    .addParameter("sort", "time")
                    .addParameter("start", String.valueOf(start))
                    .addParameter("mode", "list");
            Document document = getDocument(builder, SnapshotStrategy.ALWAYS_UPDATE);
            boolean done = false;
            String listClass = ".list-view";
            for (Element li : document.selectFirst(listClass).select(CssSelector.TAG_LI)) {
                Element div = li.selectFirst(".title");
                String href = div.selectFirst(CssSelector.TAG_A).attr("href");
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
            Matcher matcher = RegexUtils.matchesOrElseThrow(COLLECTIONS_PAGE_REGEX, numStr);
            if (start >= Integer.parseInt(matcher.group(3)) || done) {
                break;
            }
        }
        log.info("Collected {}: {}", catalog, map.size());
        return map;
    }

    /**
     * Obtains ids of collected creators of the given user.
     *
     * @param userId  id of the user which returned by {@link #user()}
     * @param catalog movie/book/music/...
     */
    public final List<Long> collectUserCreators(long userId, CatalogEnum catalog) throws HttpResponseException {
        log.info("Collect {} of user {}", catalog.getCreator().getPath(), userId);
        List<Long> ids = new LinkedList<>();
        int start = 0;
        while (true) {
            RequestBuilder builder = builder(catalog.getPath(), "/people/%d/%s", userId, catalog.getCreator().getPath())
                    .addParameter("start", String.valueOf(start));
            Document document = getDocument(builder, SnapshotStrategy.ALWAYS_UPDATE);
            String itemClass = ".item";
            for (Element div : document.select(itemClass)) {
                Element a = div.selectFirst(".title").selectFirst(CssSelector.TAG_A);
                String href = a.attr("href");
                ids.add(Long.parseLong(StringUtils.substringAfterLast(StringUtils.strip(href, "/"), "/")));
                start++;
            }
            Matcher matcher = RegexUtils.matchesOrElseThrow(CREATORS_PAGE_TITLE_REGEX, document.title().strip());
            if (start >= Integer.parseInt(matcher.group(1))) {
                break;
            }
        }
        log.info("Collected {} {}", ids.size(), catalog.getCreator().getPath());
        return ids;
    }

    /**
     * Obtains id of Douban by searching id of IMDb.
     * <p>
     *
     * @throws LoginException if not logged in first.
     */
    @Nullable
    public final Long getDbIdByImdbId(String imdbId) throws LoginException, HttpResponseException {
        if (user() == null) {
            throw new LoginException("Please log in first.");
        }
        AssertUtils.requireNotBlank(imdbId);
        CatalogEnum cat = CatalogEnum.MOVIE;
        RequestBuilder builder = create(HttpPost.METHOD_NAME, cat.getPath(), "/new_subject")
                .addParameter("ck", Objects.requireNonNull(getCookie("ck")).getValue())
                .addParameter("type", "0")
                .addParameter("p_title", imdbId)
                .addParameter("p_uid", imdbId)
                .addParameter("cat", String.valueOf(cat.getCode()))
                .addParameter("subject_submit", "下一步");
        Document document = getDocument(builder, SnapshotStrategy.NEVER_UPDATE);

        Element fieldset = document.selectFirst("div#content").selectFirst(CssSelector.TAG_FIELDSET);
        Element input = fieldset.selectFirst("input#p_uid");
        if (input == null) {
            return null;
        }
        Element span = input.nextElementSibling();
        Element ref = span.nextElementSibling();
        if (ref == null) {
            log.error(span.text());
            return null;
        }
        String href = ref.attr(CssSelector.ATTR_HREF);
        return Long.parseLong(RegexUtils.matchesOrElseThrow(URL_MOVIE_SUBJECT_REGEX, href).group("id"));
    }

    /**
     * Search items by the given keyword under the given catalog module.
     *
     * @param catalog which catalog, not null
     * @param keyword not blank
     */
    public List<SearchItem> searchSubject(@Nonnull CatalogEnum catalog, String keyword) throws HttpResponseException {
        if (StringUtils.isBlank(keyword)) {
            throw new IllegalArgumentException("Keyword mustn't be blank.");
        }
        RequestBuilder builder = builder("search", "/%s/subject_search", catalog.getPath())
                .addParameter("search_text", keyword)
                .addParameter("cat", String.valueOf(catalog.getCode()));
        Document document = getDocument(builder, SnapshotStrategy.ALWAYS_UPDATE);
        return document.select("div.item-root").stream()
                .map(div -> {
                    Element a = div.selectFirst("a.title-text");
                    String url = a.attr(CssSelector.ATTR_HREF);
                    return new SearchItem(Parsers.parseDbId(url), a.text().strip(), url);
                }).collect(Collectors.toList());
    }

    /**
     * Search items by the given keyword globally.
     *
     * @param catalog which catalog, may null
     * @param keyword not blank
     */
    public List<SearchItem> search(@Nullable CatalogEnum catalog, String keyword) throws HttpResponseException {
        if (StringUtils.isBlank(keyword)) {
            throw new IllegalArgumentException("Keyword mustn't be blank.");
        }
        RequestBuilder builder = builder0("/search").addParameter("q", keyword);
        if (catalog != null) {
            builder.addParameter("cat", String.valueOf(catalog.getCode()));
        }
        Document document = getDocument(builder, SnapshotStrategy.ALWAYS_UPDATE);
        return document.selectFirst("div.search-result").select("div.result").stream()
                .map(div -> {
                    Element a = div.selectFirst(CssSelector.TAG_H3).selectFirst(CssSelector.TAG_A);
                    Matcher matcher = RegexUtils.matchesOrElseThrow(SEARCH_ITEM_HREF_REGEX, a.attr(CssSelector.ATTR_HREF));
                    String url = URLDecoder.decode(matcher.group("url"), Constants.UTF_8);
                    return new SearchItem(Parsers.parseDbId(url), a.text().strip(), url);
                }).collect(Collectors.toList());
    }
}
