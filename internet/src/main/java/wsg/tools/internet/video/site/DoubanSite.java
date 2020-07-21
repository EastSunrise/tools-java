package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.deserializer.EnumAkaDeserializers;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.video.entity.douban.*;
import wsg.tools.internet.video.enums.*;
import wsg.tools.internet.video.jackson.deserializer.CountryAkaDeserializer;
import wsg.tools.internet.video.jackson.deserializer.LanguageAkaDeserializer;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Arrays;
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
public class DoubanSite extends AbstractVideoSite {

    public static final LocalDate START_DATE = LocalDate.of(2005, 3, 6);
    private static final Pattern IMDB_REGEX = Pattern.compile("https://www.imdb.com/title/(tt\\d{7,})");
    private static final Pattern CREATORS_PAGE_TITLE_REGEX = Pattern.compile("[^()\\s]+\\((\\d)+\\)");

    private final String apiKey;

    public DoubanSite(String apiKey) {
        super("Douban", "douban.com", 1);
        this.apiKey = apiKey;
    }

    /**
     * Returns user collected subjects data since startDate.
     */
    public List<DoubanSubject> collectUserMovies(long userId, LocalDate startDate) throws HttpResponseException {
        log.info("Collect movies of user {} since {}", userId, startDate);
        List<DoubanSubject> subjects = new LinkedList<>();
        for (MarkEnum mark : MarkEnum.values()) {
            int start = 0;
            while (true) {
                boolean done = false;
                PageResult<DoubanSubject> pageResult = parseCollectionsPage(userId, CatalogEnum.MOVIE, mark, start);
                for (DoubanSubject subject : pageResult.data) {
                    if (!subject.getMarkDate().isBefore(startDate)) {
                        subjects.add(subject);
                    } else {
                        done = true;
                        break;
                    }
                }
                start += pageResult.count;
                if (start >= pageResult.total || done) {
                    break;
                }
            }
        }
        log.info("Collected {} movies", subjects.size());
        return subjects;
    }

    /**
     * Returns user collected celebrities data.
     */
    public List<Celebrity> moviePeopleCelebrities(long userId) throws HttpResponseException {
        log.info("Collect celebrities of user {}", userId);
        List<Celebrity> celebrities = new LinkedList<>();
        int start = 0;
        while (true) {
            PageResult<Celebrity> pageResult = parseCreatorsPage(userId, CatalogEnum.MOVIE, start);
            celebrities.addAll(pageResult.data);
            start += pageResult.count;
            if (start >= pageResult.total) {
                break;
            }
        }
        log.info("Collected {} movies", celebrities.size());
        return celebrities;
    }

    /**
     * Returns user collected subjects data.
     */
    public List<DoubanSubject> moviePeopleSubjects(long userId, MarkEnum mark) throws HttpResponseException {
        log.info("Collected movies of {} from user {}", mark.getPath(), userId);
        List<DoubanSubject> subjects = new LinkedList<>();
        int start = 0;
        while (true) {
            PageResult<DoubanSubject> pageResult = parseCollectionsPage(userId, CatalogEnum.MOVIE, mark, start);
            subjects.addAll(pageResult.data);
            start += pageResult.count;
            if (start >= pageResult.total) {
                break;
            }
        }
        log.info("Collected {} movies", subjects.size());
        return subjects;
    }

    /**
     * Obtains movie subject through api, with id of IMDb acquired by parsing the web page of the subject.
     * <p>
     * This method can't obtain x-rated subjects probably which are restricted to be accessed only after logging in.
     */
    public DoubanSubject movieSubject(long dbId) throws HttpResponseException {
        DoubanSubject subject = getApiObject(String.format("/v2/movie/subject/%d", dbId), DoubanSubject.class);
        subject.setImdbId(parseSubjectPage(dbId).getImdbId());
        return subject;
    }

    public List<Photo> apiMovieSubjectPhotos(long subjectId) throws HttpResponseException {
        return getApiObjects("/v2/movie/subject/%d/photos", subjectId);
    }

    public List<Review> apiMovieSubjectReviews(long subjectId) throws HttpResponseException {
        return getApiObjects("/v2/movie/subject/%d/reviews", subjectId);
    }

    public List<Comment> apiMovieSubjectComments(long subjectId) throws HttpResponseException {
        return getApiObjects("/v2/movie/subject/%d/comments", subjectId);
    }

    public Celebrity apiMovieCelebrity(long id) throws HttpResponseException {
        return getApiObject(String.format("/v2/movie/celebrity/%d", id), Celebrity.class);
    }

    public List<Photo> apiMovieCelebrityPhotos(long celebrityId) throws HttpResponseException {
        return getApiObjects("/v2/movie/celebrity/%d/photos", celebrityId);
    }

    public List<DoubanSubject> apiMovieCelebrityWorks(long celebrityId) throws HttpResponseException {
        return getApiObjects("/v2/movie/celebrity/%d/works", celebrityId);
    }

    public List<DoubanSubject> apiMovieTop250() throws HttpResponseException {
        return getApiObjects("/v2/movie/top250");
    }

    public List<DoubanSubject> apiMovieWeekly() throws HttpResponseException {
        return getApiObjects("/v2/movie/weekly");
    }

    public List<DoubanSubject> apiMovieNewMovies() throws HttpResponseException {
        return getApiObjects("/v2/movie/new_movies");
    }

    public List<DoubanSubject> apiMovieComingSoon() throws HttpResponseException {
        return getApiObjects("/v2/movie/coming_soon");
    }

    public List<DoubanSubject> apiMovieInTheaters(City city) throws HttpResponseException {
        return getApiObjects("/v2/movie/in_theaters", Parameter.of("city", city.getNo()));
    }

    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = super.getObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.registerModule(new SimpleModule()
                .addDeserializer(Language.class, LanguageAkaDeserializer.INSTANCE)
                .addDeserializer(Country.class, CountryAkaDeserializer.INSTANCE)
                .addDeserializer(GenreEnum.class, EnumAkaDeserializers.getStringDeserializer(GenreEnum.class))
                .addDeserializer(SubtypeEnum.class, EnumAkaDeserializers.getStringDeserializer(SubtypeEnum.class))
        ).registerModule(new JavaTimeModule()
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(Constants.STANDARD_DATE_TIME_FORMATTER))
        );
        return objectMapper;
    }

    /**
     * Parse pages of subjects.
     * <p>
     * This is supplement of {@link #movieSubject(long)} (long)} to get IMDb identity of a subject.
     * <p>
     * Same as {@link #movieSubject(long)} (long)}, this method can't obtain x-rated subject probably.
     */
    private DoubanSubject parseSubjectPage(long dbId) throws HttpResponseException {
        Document document;
        try {
            document = getDocument(buildUri("/subject/" + dbId, "movie").build());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        DoubanSubject subject = new DoubanSubject();
        subject.setDbId(dbId);
        Element wrapper = document.getElementById("wrapper");

        Elements spans = wrapper.selectFirst("div#info").select("span.pl");
        Map<String, Element> spanMap = spans.stream().collect(Collectors.toMap(span -> span.text().strip(), span -> span));
        Element span;

        // title and original title
        String[] keywords = document.selectFirst("meta[name=keywords]").attr("content").split(",");
        subject.setTitle(keywords[0].strip());
        subject.setOriginalTitle(keywords[1].strip());

        // year
        String yearStr = StringUtils.strip(wrapper.selectFirst("h1").selectFirst("span.year").text(), "( )");
        subject.setYear(Year.of(Integer.parseInt(yearStr)));

        span = spanMap.get("IMDb链接:");
        if (span != null) {
            if ((span = span.nextElementSibling()).is(HTML_A)) {
                Matcher matcher = AssertUtils.matches(IMDB_REGEX, span.attr("href"));
                subject.setImdbId(matcher.group(1));
            }
        }

        return subject;
    }

    /**
     * Parse pages of user collections to get user data.
     *
     * @param catalog movie/book/music/...
     * @param mark    wish/do/collect
     * @param start   start index
     */
    private PageResult<DoubanSubject> parseCollectionsPage(long userId, CatalogEnum catalog, MarkEnum mark, int start) throws HttpResponseException {
        URI uri;
        try {
            uri = buildUri(String.format("/people/%d/%s", userId, mark.getPath()), catalog.getPath(),
                    Parameter.of("sort", "time"), Parameter.of("start", start), Parameter.of("mode", "list")).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Document document = getDocument(uri, false);
        List<DoubanSubject> subjects = new LinkedList<>();
        String listClass = ".list-view";
        for (Element li : document.selectFirst(listClass).select(HTML_LI)) {
            Element div = li.selectFirst(".title");
            Element a = div.selectFirst(">a");
            List<String> titles = Arrays.stream(a.text().strip().split("/")).map(String::strip).collect(Collectors.toList());
            DoubanSubject subject = new DoubanSubject();
            String href = a.attr("href");
            subject.setDbId(Long.parseLong(StringUtils.substringAfterLast(StringUtils.strip(href, "/"), "/")));
            subject.setTitle(titles.get(0));
            subject.setMarkDate(LocalDate.parse(div.nextElementSibling().text().strip()));
            subject.setMark(mark);
            subjects.add(subject);
        }

        String numStr = document.selectFirst("span.subject-num").text().strip();
        String[] parts = StringUtils.split(numStr, "/- ");
        return new PageResult<>(Integer.parseInt(parts[0]) - 1,
                Integer.parseInt(parts[1]) - Integer.parseInt(parts[0]) + 1, Integer.parseInt(parts[2]), subjects);
    }

    /**
     * Parse pages of user collected creators.
     *
     * @param catalog movie/book/music/...
     * @param start   start index
     */
    private PageResult<Celebrity> parseCreatorsPage(long userId, CatalogEnum catalog, int start) throws HttpResponseException {
        URI uri;
        try {
            uri = buildUri(String.format("/people/%d/%s", userId, catalog.getCreator().getPath()), catalog.getPath(),
                    Parameter.of("start", start)).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Document document = getDocument(uri, false);
        List<Celebrity> celebrities = new LinkedList<>();
        String itemClass = ".item";
        for (Element div : document.select(itemClass)) {
            Element a = div.selectFirst(".title").selectFirst(HTML_A);
            Celebrity celebrity = new Celebrity();
            String href = a.attr("href");
            celebrity.setDbId(Long.parseLong(StringUtils.substringAfterLast(StringUtils.strip(href, "/"), "/")));
            celebrities.add(celebrity);
        }

        Element title = document.selectFirst(HTML_TITLE);
        Matcher matcher = AssertUtils.matches(CREATORS_PAGE_TITLE_REGEX, title.text().strip());
        return new PageResult<>(start, celebrities.size(), Integer.parseInt(matcher.group(1)), celebrities);
    }

    private <T> T getApiObject(String path, Class<T> clazz, Parameter... params) throws HttpResponseException {
        URIBuilder builder = super.buildUri(path, "api", params);
        builder.addParameter("apikey", apiKey);
        try {
            return getObject(builder.build(), clazz);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> getApiObjects(String path, Object... pathArgs) throws HttpResponseException {
        return getApiObjects(path, null, pathArgs);
    }

    private <T> List<T> getApiObjects(String path, Parameter parameter, Object... pathArgs) throws HttpResponseException {
        int start = 0;
        List<T> list = new LinkedList<>();
        while (true) {
            URIBuilder builder = super.buildUri(String.format(path, pathArgs), "api", Parameter.of("start", start), parameter);
            builder.addParameter("apikey", apiKey);
            PageResult<T> pageResult;
            try {
                pageResult = getObject(builder.build(), new TypeReference<>() {});
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            list.addAll(pageResult.data);
            start += pageResult.count;
            if (start >= pageResult.count) {
                break;
            }
        }
        return list;
    }

    private static class PageResult<T> {
        final int start;
        final int count;
        final int total;
        final List<T> data;

        public PageResult(int start, int count, int total, List<T> data) {
            this.start = start;
            this.count = count;
            this.total = total;
            this.data = data;
        }
    }
}
