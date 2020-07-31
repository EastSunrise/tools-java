package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.video.entity.douban.container.ChartResult;
import wsg.tools.internet.video.entity.douban.container.ContentResult;
import wsg.tools.internet.video.entity.douban.container.RankedResult;
import wsg.tools.internet.video.entity.douban.pojo.*;
import wsg.tools.internet.video.enums.*;
import wsg.tools.internet.video.jackson.deserializer.YearDeserializerExt;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Obtains info from <a href="https://douban.com">豆瓣</a>.
 * <p>
 * An api key is required.
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
public final class DoubanSite extends BaseSite {

    public static final LocalDate DOUBAN_START_DATE = LocalDate.of(2005, 3, 6);
    private static final Pattern IMDB_REGEX = Pattern.compile("https://www.imdb.com/title/(tt\\d{7,})");
    private static final Pattern CREATORS_PAGE_TITLE_REGEX = Pattern.compile("[^()\\s]+\\((\\d+)\\)");
    private static final Pattern COLLECTIONS_PAGE_REGEX = Pattern.compile("(\\d+)-(\\d+)\\s/\\s(\\d+)");
    private static final int MAX_COUNT_ONCE = 100;

    private final String apiKey;

    public DoubanSite(String apiKey) {
        super("Douban", "douban.com", 1);
        this.apiKey = apiKey;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = super.getObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.registerModule(new SimpleModule()
                .addDeserializer(CountryEnum.class, EnumDeserializers.getAkaDeserializer(String.class, CountryEnum.class))
                .addDeserializer(LanguageEnum.class, EnumDeserializers.getAkaDeserializer(String.class, LanguageEnum.class))
                .addDeserializer(GenreEnum.class, EnumDeserializers.getTitleDeserializer(GenreEnum.class))
                .addDeserializer(SubtypeEnum.class, EnumDeserializers.getAkaDeserializer(String.class, SubtypeEnum.class))
                .addDeserializer(GenderEnum.class, EnumDeserializers.getTitleDeserializer(GenderEnum.class))
                .addDeserializer(ConstellationEnum.class, EnumDeserializers.getTitleDeserializer(ConstellationEnum.class))
                .addDeserializer(RoleEnum.class, EnumDeserializers.getTitleDeserializer(RoleEnum.class))
        ).registerModule(new JavaTimeModule()
                .addDeserializer(Year.class, YearDeserializerExt.INSTANCE)
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(Constants.STANDARD_DATE_TIME_FORMATTER))
        );
        return objectMapper;
    }

    /**
     * Obtains movie subject through api, with id of IMDb acquired by parsing the web page of the subject.
     * <p>
     * This method can't obtain x-rated subjects probably which are restricted to be accessed only after logging in.
     *
     * @return pair of (subject, IMDb id)
     */
    public Pair<Subject, String> movieSubject(long subjectId) throws HttpResponseException {
        return Pair.of(apiMovieSubject(subjectId), parseSubjectPage(subjectId));
    }

    /**
     * Returns marked subjects of the given user since the given start date.
     *
     * @param catalog movie/book/music/...
     * @param mark    wish/do/collect
     * @return map of (id, mark date)
     */
    public Map<Long, LocalDate> collectUserSubjects(long userId, LocalDate since, CatalogEnum catalog, MarkEnum mark) throws HttpResponseException {
        if (since == null) {
            since = DOUBAN_START_DATE;
        }
        log.info("Collect movies of user {} since {}", userId, since);
        Map<Long, LocalDate> map = new HashMap<>();
        int start = 0;
        while (true) {
            URI uri;
            try {
                uri = buildCatalogPath("/people/%d/%s", catalog, userId, mark.getPath())
                        .addParameter("sort", "time")
                        .addParameter("start", String.valueOf(start))
                        .addParameter("mode", "list").build();
            } catch (URISyntaxException e) {
                throw AssertUtils.runtimeException(e);
            }
            Document document = getDocument(uri, false);
            boolean done = false;
            String listClass = ".list-view";
            for (Element li : document.selectFirst(listClass).select(HTML_LI)) {
                Element div = li.selectFirst(".title");
                String href = div.selectFirst(HTML_A).attr("href");
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
        log.info("Collected {} movies", map.size());
        return map;
    }

    /**
     * Returns ids of collected creators.
     *
     * @param catalog movie/book/music/...
     */
    public List<Long> collectUserCreators(long userId, CatalogEnum catalog) throws HttpResponseException {
        log.info("Collect celebrities of user {}", userId);
        List<Long> ids = new LinkedList<>();
        int start = 0;
        while (true) {
            URI uri;
            try {
                uri = buildCatalogPath("/people/%d/%s", catalog, userId, catalog.getCreator().getPath())
                        .addParameter("start", String.valueOf(start)).build();
            } catch (URISyntaxException e) {
                throw AssertUtils.runtimeException(e);
            }
            Document document = getDocument(uri, false);
            String itemClass = ".item";
            for (Element div : document.select(itemClass)) {
                Element a = div.selectFirst(".title").selectFirst(HTML_A);
                String href = a.attr("href");
                ids.add(Long.parseLong(StringUtils.substringAfterLast(StringUtils.strip(href, "/"), "/")));
                start++;
            }
            Element title = document.selectFirst(HTML_TITLE);
            Matcher matcher = AssertUtils.matches(CREATORS_PAGE_TITLE_REGEX, title.text().strip());
            if (start >= Integer.parseInt(matcher.group(1))) {
                break;
            }
        }
        log.info("Collected {} movies", ids.size());
        return ids;
    }

    /**
     * Parse pages of subjects.
     * <p>
     * This is supplement of {@link #movieSubject(long)} (long)} to get IMDb identity of a subject.
     * <p>
     * Same as {@link #apiMovieSubject(long)} (long)}, this method can't obtain x-rated subject probably.
     *
     * @return IMDb id
     */
    private String parseSubjectPage(long subjectId) throws HttpResponseException {
        Document document;
        try {
            document = getDocument(buildCatalogPath("/subject/%d", CatalogEnum.MOVIE, subjectId).build());
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }

        Element wrapper = document.getElementById("wrapper");
        Elements spans = wrapper.selectFirst("div#info").select("span.pl");
        Map<String, Element> spanMap = spans.stream().collect(Collectors.toMap(span -> span.text().strip(), span -> span));
        Element span = spanMap.get("IMDb链接:");
        if (span != null) {
            if ((span = span.nextElementSibling()).is(HTML_A)) {
                Matcher matcher = AssertUtils.matches(IMDB_REGEX, span.attr("href"));
                return matcher.group(1);
            }
        }
        return null;
    }

    private URIBuilder buildCatalogPath(String path, CatalogEnum catalog, Object... pathArgs) {
        return super.buildPath(path, pathArgs).setHost(catalog.getPath() + "." + domain);
    }

    /**
     * This method can't obtain x-rated subjects probably which are restricted to be accessed only after logging in.
     */
    public Subject apiMovieSubject(long subjectId) throws HttpResponseException {
        return getApiObject(buildApiPath("/v2/movie/subject/%d", subjectId), Subject.class);
    }

    public Celebrity apiMovieCelebrity(long celebrityId) throws HttpResponseException {
        return getApiObject(buildApiPath("/v2/movie/celebrity/%d", celebrityId), Celebrity.class);
    }

    /**
     * It's updated every Friday.
     */
    public RankedResult apiMovieWeekly() throws HttpResponseException {
        return getApiObject(buildApiPath("/v2/movie/weekly"), RankedResult.class, false);
    }

    public Pair<String, List<SimpleSubject>> apiMovieTop250() throws HttpResponseException {
        return getApiChart(buildApiPath("/v2/movie/top250"));
    }

    /**
     * todo Current time
     */
    public Pair<String, List<SimpleSubject>> apiMovieNewMovies() throws HttpResponseException {
        return getApiChart(buildApiPath("/v2/movie/new_movies"));
    }

    public Pair<String, List<SimpleSubject>> apiMovieInTheaters(CityEnum city) throws HttpResponseException {
        return getApiChart(buildApiPath("/v2/movie/in_theaters").addParameter("city", city.getPath()));
    }

    public Pair<String, List<SimpleSubject>> apiMovieComingSoon() throws HttpResponseException {
        return getApiChart(buildApiPath("/v2/movie/coming_soon"));
    }

    /**
     * Get chart list of subjects.
     *
     * @return pair of title-subjects
     */
    private Pair<String, List<SimpleSubject>> getApiChart(URIBuilder builder) throws HttpResponseException {
        int start = 0;
        List<SimpleSubject> subjects = new LinkedList<>();
        String title;
        builder.addParameter("count", String.valueOf(MAX_COUNT_ONCE));
        while (true) {
            builder.setParameter("start", String.valueOf(start));
            ChartResult chartResult = getApiObject(builder, ChartResult.class, false);
            subjects.addAll(chartResult.getContent());
            title = chartResult.getTitle();
            start += chartResult.getCount();
            if (start >= chartResult.getTotal()) {
                break;
            }
        }
        return Pair.of(title, subjects);
    }

    /**
     * Only include official photos.
     */
    public Pair<SimpleSubject, List<Photo>> apiMovieSubjectPhotos(long subjectId) throws HttpResponseException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/subject/%d/photos", subjectId);
    }

    public Pair<SimpleSubject, List<Review>> apiMovieSubjectReviews(long subjectId) throws HttpResponseException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/subject/%d/reviews", subjectId);
    }

    public Pair<SimpleSubject, List<Comment>> apiMovieSubjectComments(long subjectId) throws HttpResponseException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/subject/%d/comments", subjectId);
    }

    public Pair<SimpleCelebrity, List<Photo>> apiMovieCelebrityPhotos(long celebrityId) throws HttpResponseException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/celebrity/%d/photos", celebrityId);
    }

    public Pair<SimpleCelebrity, List<Work>> apiMovieCelebrityWorks(long celebrityId) throws HttpResponseException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/celebrity/%d/works", celebrityId);
    }

    /**
     * Get content of a subject or a celebrity, including its owner.
     *
     * @return pair of owner-content
     */
    private <O, C> Pair<O, List<C>> getApiContent(TypeReference<ContentResult<O, C>> type, String path, long ownerId) throws HttpResponseException {
        int start = 0;
        List<C> content = new LinkedList<>();
        O owner;
        while (true) {
            URIBuilder builder = buildApiPath(path, ownerId)
                    .addParameter("start", String.valueOf(start))
                    .addParameter("count", String.valueOf(MAX_COUNT_ONCE))
                    .addParameter("apikey", apiKey);
            ContentResult<O, C> contentResult;
            try {
                contentResult = getObject(builder.build(), type);
            } catch (URISyntaxException e) {
                throw AssertUtils.runtimeException(e);
            }
            content.addAll(contentResult.getContent());
            owner = contentResult.getOwner();
            start += contentResult.getCount();
            if (start >= contentResult.getTotal()) {
                break;
            }
        }
        return Pair.of(owner, content);
    }

    private <T> T getApiObject(URIBuilder builder, Class<T> clazz) throws HttpResponseException {
        return getApiObject(builder, clazz, true);
    }

    /**
     * Obtains an object from api. Append api key after the uri.
     *
     * @param <T> type of object
     * @return target object
     */
    private <T> T getApiObject(URIBuilder builder, Class<T> clazz, boolean cached) throws HttpResponseException {
        try {
            return getObject(builder.setParameter("apikey", apiKey).build(), clazz, cached);
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    private URIBuilder buildApiPath(String path, Object... pathArgs) {
        return super.buildPath(path, pathArgs).setHost("api." + domain);
    }
}
