package wsg.tools.internet.movie.douban;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.cookie.Cookie;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.deserializer.TitleEnumDeserializer;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.Loggable;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.RequestBuilder;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.LoginException;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UnexpectedContentException;
import wsg.tools.internet.common.UnexpectedException;
import wsg.tools.internet.enums.Language;
import wsg.tools.internet.movie.common.Parsers;
import wsg.tools.internet.movie.common.Runtime;
import wsg.tools.internet.movie.common.enums.DoubanCatalog;
import wsg.tools.internet.movie.common.enums.DoubanMark;
import wsg.tools.internet.movie.common.enums.MovieGenre;

/**
 * Obtains info from <a href="https://douban.com">豆瓣</a>.
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
@ConcreteSite
public class DoubanSite extends BaseSite
    implements RepoRetrievable<Long, BaseDoubanSubject>, Loggable<Long> {

    public static final LocalDate DOUBAN_START_DATE = LocalDate.of(2005, 3, 6);

    public static final Pattern URL_MOVIE_SUBJECT_REGEX =
        Pattern.compile("https://movie.douban.com/subject/(?<id>\\d{7,8})/?");

    protected static final int MAX_COUNT_ONCE = 100;

    protected static final ObjectMapper MAPPER = new ObjectMapper()
        .registerModule(new SimpleModule()
            .addDeserializer(MovieGenre.class, new TitleEnumDeserializer<>(MovieGenre.class)))
        .registerModule(new JavaTimeModule().addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(Constants.YYYY_MM_DD_HH_MM_SS)));

    private static final Pattern CREATORS_PAGE_TITLE_REGEX = Pattern
        .compile("[^()\\s]+\\((\\d+)\\)");

    private static final Pattern PAGE_TITLE_REGEX = Pattern.compile("(?<t>.*)\\s\\(豆瓣\\)");

    private static final Pattern COLLECTIONS_PAGE_REGEX = Pattern
        .compile("(\\d+)-(\\d+)\\s/\\s(\\d+)");

    private static final Pattern COOKIE_DBCL2_REGEX = Pattern
        .compile("\"(?<id>\\d+):[0-9A-Za-z+/]+\"");

    private static final Pattern SEARCH_ITEM_HREF_REGEX = Pattern.compile(
        "https://www\\.douban\\.com/link2/\\?url=(?<url>[0-9A-Za-z%.-]+)&query=(?<q>[0-9A-Za-z%]+)&cat_id=(?<cat>\\d*)&type=search&pos=(?<pos>\\d+)");

    public DoubanSite() {
        super("Douban", new BasicHttpSession("douban.com"));
    }

    /**
     * Log in the site with the given username and password.
     *
     * @throws LoginException if the given user and password is invalid or a CAPTCHA is required.
     */
    public void login(String username, String password) throws OtherResponseException {
        logout();
        findDocument(builder0(null), t -> true);
        RequestBuilder builder = create(METHOD_POST, "accounts")
            .setPath("/j/mobile/login/basic")
            .addParameter("ck", "").addParameter("name", username)
            .addParameter("password", password)
            .addParameter("remember", true);
        LoginResult result = null;
        try {
            result = getObject(builder, MAPPER, LoginResult.class, t -> true);
        } catch (NotFoundException e) {
            throw new UnexpectedException(e);
        }
        if (!result.isSuccess()) {
            throw new LoginException(result.getMessage());
        }
    }

    @Override
    public Long user() {
        Cookie cookie = getCookie("dbcl2");
        if (cookie == null) {
            return null;
        }
        return Long.parseLong(
            RegexUtils.matchesOrElseThrow(COOKIE_DBCL2_REGEX, cookie.getValue()).group("id"));
    }

    public void logout() throws OtherResponseException {
        if (user() == null) {
            return;
        }
        findDocument(builder0(null), t -> true);
        RequestBuilder builder = builder0("/accounts/logout")
            .addParameter("source", "main")
            .addParameter("ck", Objects.requireNonNull(getCookie("ck")).getValue());
        findDocument(builder, t -> true);
    }

    /**
     * Obtains a subject by the given identifier.
     * <p>
     * Some x-rated subjects may be restricted to access without logging in.
     */
    @Nonnull
    @Override
    public BaseDoubanSubject findById(@Nonnull Long id)
        throws NotFoundException, OtherResponseException {
        String subDomain = DoubanCatalog.MOVIE.name().toLowerCase(Locale.ROOT);
        RequestBuilder builder = builder(subDomain, "/subject/%d", id);
        Document document = getDocument(builder, t -> false);
        String text = document.selectFirst("script[type=application/ld+json]").html();
        text = StringUtils.replaceChars(text, "\n\t", "");
        BaseDoubanSubject subject;
        try {
            subject = MAPPER.readValue(text, BaseDoubanSubject.class);
        } catch (JsonProcessingException e) {
            throw new UnexpectedException(e);
        }

        subject.setId(id);
        String zhTitle = RegexUtils.matchesOrElseThrow(PAGE_TITLE_REGEX, document.title())
            .group("t");
        subject.setZhTitle(zhTitle);
        String name = subject.getName().replace("  ", " ");
        if (name.startsWith(zhTitle)) {
            if (name.length() > zhTitle.length()) {
                subject.setOriginalTitle(
                    StringEscapeUtils.unescapeHtml4(name.substring(zhTitle.length()).strip()));
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
                Elements options = season.select(CssSelectors.TAG_OPTION);
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
        extractInfo(subject,
            info.select("span.pl").stream().collect(Collectors.toMap(Element::text, e -> e)));
        return subject;
    }

    private void extractInfo(BaseDoubanSubject subject, Map<String, Element> spans) {
        Element ls = spans.get("语言:");
        if (null != ls) {
            String[] languages = StringUtils.split(((TextNode) ls.nextSibling()).text(), "/");
            subject.setLanguages(Arrays.stream(languages)
                .map(language -> EnumUtilExt.valueOfAka(Language.class, language.strip()))
                .collect(Collectors.toList()));
        }
        Element imdb = spans.get("IMDb链接:");
        if (imdb != null) {
            subject.setImdbId(imdb.nextElementSibling().text().strip());
        }

        if (subject instanceof DoubanMovie) {
            DoubanMovie movie = (DoubanMovie) subject;
            final String plDuration = "片长:";
            movie.setRuntimes(getRuntimes(spans.get(plDuration)));
        }

        if (subject instanceof DoubanSeries) {
            DoubanSeries series = (DoubanSeries) subject;
            Element episodes = spans.get("集数:");
            if (null != episodes) {
                series.setEpisodesCount(
                    Integer.parseInt(((TextNode) episodes.nextSibling()).text().strip()));
            }
            final String plDuration = "单集片长:";
            series.setRuntimes(getRuntimes(spans.get(plDuration)));
        }
    }

    private List<Runtime> getRuntimes(Element span) {
        if (span != null) {
            Element element = span.nextElementSibling();
            Node node = element.is(CssSelectors.TAG_SPAN) ? element.nextSibling()
                : element.previousSibling();
            if (node instanceof TextNode) {
                String[] parts = StringUtils.strip(((TextNode) node).text(), " /").split("/");
                return Arrays.stream(parts).map(String::strip).map(Runtime::of)
                    .collect(Collectors.toList());
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
    public Map<Long, LocalDate> collectUserSubjects(long userId, LocalDate since,
        DoubanCatalog catalog, DoubanMark mark) throws NotFoundException, OtherResponseException {
        if (since == null) {
            since = DOUBAN_START_DATE;
        }
        log.info("Collect {} {} of user {} since {}", mark, catalog, userId, since);
        Map<Long, LocalDate> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        int start = 0;
        while (true) {
            RequestBuilder builder = builder(catalog.name().toLowerCase(Locale.ROOT),
                "/people/%d/%s", userId, mark.name().toLowerCase(Locale.ROOT))
                .addParameter("sort", "time")
                .addParameter("start", start)
                .addParameter("mode", "list");
            Document document = getDocument(builder, t -> true);
            boolean done = false;
            String listClass = ".list-view";
            for (Element li : document.selectFirst(listClass).select(CssSelectors.TAG_LI)) {
                Element div = li.selectFirst(".title");
                String href = div.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
                long id = Long
                    .parseLong(StringUtils.substringAfterLast(StringUtils.strip(href, "/"), "/"));
                LocalDate markDate = LocalDate.parse(div.nextElementSibling().text().strip());
                if (markDate.isBefore(since)) {
                    done = true;
                    break;
                }
                map.put(id, markDate);
                start++;
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
    public List<Long> collectUserCreators(long userId, DoubanCatalog catalog)
        throws NotFoundException, OtherResponseException {
        log.info("Collect {} of user {}", catalog.getCreator().getPlurality(), userId);
        List<Long> ids = new LinkedList<>();
        int start = 0;
        while (true) {
            RequestBuilder builder = builder(catalog.name().toLowerCase(Locale.ROOT),
                "/people/%d/%s", userId,
                catalog.getCreator().getPlurality())
                .addParameter("start", start);
            Document document = getDocument(builder, t -> true);
            String itemClass = ".item";
            for (Element div : document.select(itemClass)) {
                Element a = div.selectFirst(".title").selectFirst(CssSelectors.TAG_A);
                String href = a.attr(CssSelectors.ATTR_HREF);
                ids.add(Long.parseLong(
                    StringUtils.substringAfterLast(StringUtils.strip(href, "/"), "/")));
                start++;
            }
            Matcher matcher = RegexUtils
                .matchesOrElseThrow(CREATORS_PAGE_TITLE_REGEX, document.title().strip());
            if (start >= Integer.parseInt(matcher.group(1))) {
                break;
            }
        }
        log.info("Collected {} {}", ids.size(), catalog.getCreator().getPlurality());
        return ids;
    }

    /**
     * Obtains id of Douban by searching id of IMDb.
     * <p>
     *
     * @throws LoginException if not logged in first.
     */
    @Nullable
    public Long getDbIdByImdbId(String imdbId) throws NotFoundException, OtherResponseException {
        if (user() == null) {
            throw new LoginException("Please log in first.");
        }
        AssertUtils.requireNotBlank(imdbId);
        DoubanCatalog cat = DoubanCatalog.MOVIE;
        RequestBuilder builder = create(METHOD_POST, cat.name().toLowerCase(Locale.ROOT))
            .setPath("/new_subject")
            .addParameter("ck", Objects.requireNonNull(getCookie("ck")).getValue())
            .addParameter("type", "0")
            .addParameter("p_title", imdbId).addParameter("p_uid", imdbId)
            .addParameter("cat", String.valueOf(cat.getCode()))
            .addParameter("subject_submit", "下一步");
        Document document = getDocument(builder, t -> false);

        Element fieldset = document.selectFirst("div#content")
            .selectFirst(CssSelectors.TAG_FIELDSET);
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
        String href = ref.attr(CssSelectors.ATTR_HREF);
        return Long
            .parseLong(RegexUtils.matchesOrElseThrow(URL_MOVIE_SUBJECT_REGEX, href).group("id"));
    }

    /**
     * Search items by the given keyword under the given catalog module.
     *
     * @param catalog which catalog, not null
     * @param keyword not blank
     */
    public List<SearchItem> searchSubject(@Nonnull DoubanCatalog catalog, String keyword)
        throws NotFoundException, OtherResponseException {
        AssertUtils.requireNotBlank(keyword);
        RequestBuilder builder = builder("search", "/%s/subject_search", catalog.name().toLowerCase(
            Locale.ROOT))
            .addParameter("search_text", keyword)
            .addParameter("cat", catalog.getCode());
        Document document = getDocument(builder, t -> true);
        return document.select("div.item-root").stream().map(div -> {
            Element a = div.selectFirst("a.title-text");
            String url = a.attr(CssSelectors.ATTR_HREF);
            return new SearchItem(Parsers.parseDbId(url), a.text().strip(), url);
        }).collect(Collectors.toList());
    }

    /**
     * Search items by the given keyword globally.
     *
     * @param catalog which catalog, may null
     * @param keyword not blank
     */
    public List<SearchItem> search(@Nullable DoubanCatalog catalog, String keyword)
        throws NotFoundException, OtherResponseException {
        if (StringUtils.isBlank(keyword)) {
            throw new IllegalArgumentException("Keyword mustn't be blank.");
        }
        RequestBuilder builder = builder0("/search").addParameter("q", keyword);
        if (catalog != null) {
            builder.addParameter("cat", catalog.getCode());
        }
        Document document = getDocument(builder, t -> true);
        return document.selectFirst("div.search-result").select("div.result").stream().map(div -> {
            Element a = div.selectFirst(CssSelectors.TAG_H3).selectFirst(CssSelectors.TAG_A);
            Matcher matcher = RegexUtils
                .matchesOrElseThrow(SEARCH_ITEM_HREF_REGEX, a.attr(CssSelectors.ATTR_HREF));
            String url = URLDecoder.decode(matcher.group("url"), Constants.UTF_8);
            return new SearchItem(Parsers.parseDbId(url), a.text().strip(), url);
        }).collect(Collectors.toList());
    }
}
