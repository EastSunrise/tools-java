package wsg.tools.internet.movie.douban;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.jsoup.Jsoup;
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
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.page.FixedSizePageReq;
import wsg.tools.internet.base.support.AbstractLoggableSite;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.LoginException;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UnexpectedContentException;
import wsg.tools.internet.common.UnexpectedException;
import wsg.tools.internet.common.enums.Language;
import wsg.tools.internet.movie.common.Runtime;
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
public class DoubanSite extends AbstractLoggableSite<Long> implements DoubanRepository {

    public static final Pattern URL_MOVIE_SUBJECT_REGEX =
        Pattern.compile("https://movie.douban.com/subject/(?<id>\\d{7,8})/?");
    protected static final int MAX_COUNT_ONCE = 100;

    public DoubanSite() {
        this("");
    }

    protected DoubanSite(String subHost) {
        super("Douban",
            httpsHost((StringUtils.isBlank(subHost) ? "" : subHost + ".") + "douban.com"));
    }

    @Override
    public void login(String username, String password)
        throws OtherResponseException, LoginException {
        logout();
        RequestBuilder builder = create("accounts", METHOD_POST, "/j/mobile/login/basic")
            .addParameter("ck", "")
            .addParameter("remember", String.valueOf(true))
            .addParameter("name", username)
            .addParameter("password", password);
        LoginResult result = null;
        try {
            result = getObject(builder, Lazy.MAPPER, LoginResult.class);
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
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.COOKIE_DBCL2_REGEX, cookie.getValue());
        return Long.parseLong(matcher.group("id"));
    }

    @Override
    public void logout() throws OtherResponseException {
        if (user() == null) {
            return;
        }
        findDocument(httpGet(""));
        RequestBuilder builder = httpGet("/accounts/logout")
            .addParameter("source", "main")
            .addParameter("ck", Objects.requireNonNull(getCookie("ck")).getValue());
        findDocument(builder);
    }

    @Nonnull
    @Override
    public DoubanPageResult<SubjectIndex> searchGlobally(String keyword,
        @Nonnull FixedSizePageReq req, @Nullable DoubanCatalog catalog)
        throws OtherResponseException {
        AssertUtils.requireNotBlank(keyword);
        RequestBuilder builder = httpGet("/j/search")
            .addParameter("q", keyword)
            .addParameter("start", String.valueOf(req.getCurrent() * 20));
        if (catalog != null) {
            builder.addParameter("cat", String.valueOf(catalog.getCode()));
        }
        SearchResult result = null;
        try {
            result = getObject(builder, Lazy.MAPPER, SearchResult.class);
        } catch (NotFoundException e) {
            throw new UnexpectedException(e);
        }
        if (result.getMsg() != null) {
            throw new OtherResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR, result.getMsg());
        }
        List<SubjectIndex> subjects = new ArrayList<>(result.getItems().size());
        for (String item : result.getItems()) {
            Element nbg = Jsoup.parse(item).body().selectFirst(".nbg");
            String href = nbg.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.SUBJECT_LINK_REGEX, href);
            String url = URLDecoder.decode(matcher.group("u"), Constants.UTF_8);
            Matcher urlMatcher = Lazy.SUBJECT_URL_REGEX.matcher(url);
            if (!urlMatcher.matches()) {
                log.info("Not a subject: {}", url);
                continue;
            }
            long id = Long.parseLong(urlMatcher.group("id"));
            int code = Integer.parseInt(matcher.group("c"));
            DoubanCatalog cat = EnumUtilExt.valueOfIntCode(DoubanCatalog.class, code);
            subjects.add(new BasicSubject(id, cat, nbg.attr(CssSelectors.ATTR_TITLE)));
        }
        return new DoubanPageResult<>(subjects, req, result.getTotal(), 20);
    }

    @Nonnull
    @Override
    public List<SubjectIndex> search(@Nonnull DoubanCatalog catalog, String keyword)
        throws OtherResponseException {
        AssertUtils.requireNotBlank(keyword);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Nonnull
    @Override
    public DoubanPageResult<MarkedSubject> findUserSubjects(@Nonnull DoubanCatalog catalog,
        long userId, @Nonnull DoubanMark mark, @Nonnull FixedSizePageReq req)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = create(catalog.getAsPath(), METHOD_GET,
            "/people/%d/%s", userId, mark.getAsPath())
            .addParameter("start", String.valueOf(req.getCurrent() * 30))
            .addParameter("sort", "time")
            .addParameter("rating", "all")
            .addParameter("filter", "all")
            .addParameter("mode", "list");
        Document document = getDocument(builder);
        Elements lis = document.selectFirst(".list-view").select(".item");
        List<MarkedSubject> subjects = new ArrayList<>(lis.size());
        for (Element li : lis) {
            long id = Long.parseLong(li.id().substring(4));
            String title = li.selectFirst(CssSelectors.TAG_A).text().split("/")[0].strip();
            LocalDate markedDate = LocalDate.parse(li.selectFirst(".date").text());
            subjects.add(new MarkedSubject(id, catalog, title, markedDate));
        }
        String numStr = document.selectFirst(".subject-num").text();
        int total = Integer.parseInt(numStr.split("/")[1].strip());
        return new DoubanPageResult<>(subjects, req, total, 30);
    }

    @Nonnull
    @Override
    public DoubanPageResult<PersonIndex> findUserCreators(@Nonnull DoubanCatalog catalog,
        long userId, @Nonnull FixedSizePageReq req)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = create(catalog.getAsPath(), METHOD_GET,
            "/people/%d/%s", userId, catalog.getPersonPlurality())
            .addParameter("start", String.valueOf(req.getCurrent() * 15));
        Document document = getDocument(builder);
        Elements items = document.select(".item");
        List<PersonIndex> indices = new ArrayList<>(items.size());
        for (Element item : items) {
            Element a = item.selectFirst(CssSelectors.TAG_A);
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.CREATOR_URL_REGEX, href);
            long id = Long.parseLong(matcher.group("id"));
            indices.add(new BasicPerson(id, catalog, a.attr(CssSelectors.ATTR_TITLE)));
        }
        String title = document.title();
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.CREATORS_PAGE_TITLE_REGEX, title);
        long total = Long.parseLong(matcher.group("t"));
        return new DoubanPageResult<>(indices, req, total, 15);
    }

    @Nonnull
    @Override
    public AbstractMovie findMovieById(long id)
        throws NotFoundException, OtherResponseException {
        return getSubject(DoubanCatalog.MOVIE, id,
            (subject, document) -> {
                AbstractMovie movie = (AbstractMovie) subject;
                String title = document.title();
                movie.setZhTitle(title.substring(0, title.length() - 5));
                String name = movie.getName().replace("  ", " ");
                if (name.startsWith(movie.getZhTitle())) {
                    if (name.length() > movie.getZhTitle().length()) {
                        String ot = name.substring(movie.getZhTitle().length());
                        movie.setOriginalTitle(StringEscapeUtils.unescapeHtml4(ot.strip()));
                    }
                } else {
                    throw new UnexpectedContentException("Name and zhTitle are not matched.");
                }
                String year = StringUtils.strip(document.selectFirst(".year").text(), "()");
                movie.setYear(Integer.parseInt(year));
                Element rating = document.selectFirst("div.rating_right");
                movie.setReleased(!rating.hasClass("not_showed"));

                Map<String, Element> metadata = getMetadata(document);
                Element ls = metadata.get("语言:");
                if (null != ls) {
                    String[] languages = StringUtils
                        .split(((TextNode) ls.nextSibling()).text(), "/");
                    movie.setLanguages(Arrays.stream(languages)
                        .map(language -> EnumUtilExt.valueOfAlias(Language.class, language.strip()))
                        .collect(Collectors.toList()));
                }
                if (movie instanceof DoubanMovie) {
                    movie.setRuntimes(getRuntimes(metadata.get("片长:")));
                }
                if (movie instanceof DoubanSeries) {
                    DoubanSeries series = (DoubanSeries) movie;
                    Element episodes = metadata.get("集数:");
                    if (null != episodes) {
                        String episodesCount = ((TextNode) episodes.nextSibling()).text().strip();
                        series.setEpisodesCount(Integer.parseInt(episodesCount));
                    }
                    movie.setRuntimes(getRuntimes(metadata.get("单集片长:")));

                    Element season = document.selectFirst("#season");
                    if (season != null) {
                        Elements options = season.select(CssSelectors.TAG_OPTION);
                        long[] seasons = new long[options.size()];
                        for (Element option : options) {
                            int currentSeason = Integer.parseInt(option.text()) - 1;
                            seasons[currentSeason] = Long.parseLong(option.val());
                            if (option.hasAttr("selected")) {
                                series.setCurrentSeason(Integer.parseInt(option.text()));
                            }
                        }
                        series.setSeasons(seasons);
                    }
                }
                Element imdb = metadata.get("IMDb:");
                if (imdb != null) {
                    movie.setImdbId(((TextNode) imdb.nextSibling()).text().strip());
                }
                return movie;
            });
    }

    @Override
    public DoubanBook findBookById(long id) throws NotFoundException, OtherResponseException {
        return getSubject(DoubanCatalog.BOOK, id, (subject, document) -> (DoubanBook) subject);
    }

    private <T extends AbstractSubject> T getSubject(DoubanCatalog catalog, long id,
        BiFunction<AbstractSubject, Document, T> decorator)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = create(catalog.getAsPath(), METHOD_GET, "/subject/%d/", id);
        Document document = getDocument(builder);
        String text = document.selectFirst("script[type=application/ld+json]").html();
        text = StringUtils.replaceChars(text, "\n\t", "");
        AbstractSubject subject;
        try {
            subject = Lazy.MAPPER.readValue(text, AbstractSubject.class);
        } catch (JsonProcessingException e) {
            throw new UnexpectedException(e);
        }
        subject.setId(id);
        return decorator.apply(subject, document);
    }

    private Map<String, Element> getMetadata(Document document) {
        Elements pls = document.selectFirst("#info").select(".pl");
        return pls.stream()
            .collect(Collectors.toMap(Element::text, Function.identity()));
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

    @Override
    public long getDbIdByImdbId(String imdbId)
        throws NotFoundException, OtherResponseException, LoginException {
        if (user() == null) {
            throw new LoginException("Please log in first.");
        }
        AssertUtils.requireNotBlank(imdbId);
        String substation = DoubanCatalog.MOVIE.getAsPath();
        RequestBuilder builder = create(substation, METHOD_POST, "/new_subject")
            .addParameter("ck", Objects.requireNonNull(getCookie("ck")).getValue())
            .addParameter("type", "0")
            .addParameter("p_title", imdbId)
            .addParameter("p_uid", imdbId)
            .addParameter("cat", String.valueOf(DoubanCatalog.MOVIE.getCode()))
            .addParameter("subject_submit", "下一步");
        Document document = findDocument(builder);
        Element fieldset = document.selectFirst("div#content")
            .selectFirst(CssSelectors.TAG_FIELDSET);
        Element input = fieldset.selectFirst("input#p_uid");
        if (input == null) {
            throw new NotFoundException("");
        }
        Element span = input.nextElementSibling();
        Element ref = span.nextElementSibling();
        if (ref == null) {
            throw new NotFoundException(span.text());
        }
        String href = ref.attr(CssSelectors.ATTR_HREF);
        Matcher matcher = RegexUtils.matchesOrElseThrow(URL_MOVIE_SUBJECT_REGEX, href);
        return Long.parseLong(matcher.group("id"));
    }

    private static final class Lazy {

        private static final Pattern COOKIE_DBCL2_REGEX = Pattern
            .compile("\"(?<id>\\d+):[0-9A-Za-z+/]+\"");
        private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new SimpleModule()
                .addDeserializer(MovieGenre.class, EnumDeserializers.ofAlias(MovieGenre.class)))
            .registerModule(new JavaTimeModule().addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(Constants.YYYY_MM_DD_HH_MM_SS)));
        private static final Pattern SUBJECT_LINK_REGEX = Pattern.compile(
            "https://www\\.douban\\.com/link2/\\?url=(?<u>[\\w%.-]+)"
                + "&query=(?<q>[\\w%-]+)&cat_id=(?<c>\\d+)&type=search&pos=(?<p>\\d+)");
        private static final Pattern CREATORS_PAGE_TITLE_REGEX = Pattern
            .compile("[^()\\s]+\\((?<t>\\d+)\\)");
        private static final Pattern SUBJECT_URL_REGEX;
        private static final Pattern CREATOR_URL_REGEX;

        static {
            String catalogs = Arrays.stream(DoubanCatalog.values()).map(PathSupplier::getAsPath)
                .collect(Collectors.joining("|"));
            SUBJECT_URL_REGEX = Pattern
                .compile("https://(" + catalogs + ")\\.douban\\.com/subject/(?<id>\\d+)/");
            String creators = Arrays.stream(DoubanCatalog.values()).map(DoubanCatalog::getPerson)
                .collect(Collectors.joining("|"));
            CREATOR_URL_REGEX = Pattern.compile(
                "https://(" + catalogs + ")\\.douban\\.com/(" + creators + ")/(?<id>\\d+)/");
        }
    }
}
