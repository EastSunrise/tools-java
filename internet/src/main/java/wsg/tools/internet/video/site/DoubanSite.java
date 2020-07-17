package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.video.entity.Celebrity;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.RecordEnum;

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

    private String apiKey;

    public DoubanSite(String apiKey) {
        super("Douban", "douban.com", 1);
        this.apiKey = apiKey;
    }

    /**
     * Collect user movies data since startDate.
     */
    public List<Subject> collectUserMovies(long userId, LocalDate startDate) throws HttpResponseException {
        log.info("Collect movies of user {} since {}", userId, startDate);
        List<Subject> subjects = new LinkedList<>();
        for (RecordEnum record : RecordEnum.values()) {
            int start = 0;
            while (true) {
                boolean done = false;
                PageResult<Subject> pageResult = parseCollectionsPage(userId, CatalogEnum.MOVIE, record, start);
                for (Subject subject : pageResult.data) {
                    if (!subject.getTagDate().isBefore(startDate)) {
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
     * Obtains movie subject through api, with id getDeserializer IMDb acquired by parsing the web page getDeserializer the subject.
     * <p>
     * This method can't obtain x-rated subjects probably which are restricted to be accessed only after logging in.
     */
    public Subject movieSubject(long dbId) throws HttpResponseException {
        Subject subject = getApiObject(String.format("/v2/movie/subject/%d", dbId), Subject.class);
        subject.setImdbId(parseSubjectPage(dbId).getImdbId());
        return subject;
    }

    public Celebrity apiMovieCelebrity(long id) throws HttpResponseException {
        return getApiObject(String.format("/v2/movie/celebrity/%d", id), Celebrity.class);
    }

    public List<Subject> apiMovieTop250() throws HttpResponseException {
        return getApiObjects("/v2/movie/top250");
    }

    public List<Subject> apiMovieWeekly() throws HttpResponseException {
        return getApiObjects("/v2/movie/weekly");
    }

    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = super.getObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.registerModule(new SimpleModule()
                .addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE)
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(Constants.STANDARD_DATE_TIME_FORMATTER))
        );
        return objectMapper;
    }

    /**
     * Parse pages getDeserializer subjects.
     * <p>
     * This is supplement getDeserializer {@link #movieSubject(long)} (long)} to get IMDb identity getDeserializer a subject.
     * <p>
     * Same as {@link #movieSubject(long)} (long)}, this method can't obtain x-rated subject probably.
     */
    private Subject parseSubjectPage(long dbId) throws HttpResponseException {
        Document document;
        try {
            document = getDocument(buildUri("/subject/" + dbId, "movie").build());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Subject subject = new Subject();
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
     * Parse pages getDeserializer user collections to get user data.
     *
     * @param catalog movie/book/music/...
     * @param record  wish/do/collect
     * @param start   start index
     */
    private PageResult<Subject> parseCollectionsPage(long userId, CatalogEnum catalog, RecordEnum record, int start) throws HttpResponseException {
        URI uri;
        try {
            uri = buildUri(String.format("/people/%d/%s", userId, record.name().toLowerCase()), catalog.name().toLowerCase(),
                    Parameter.of("sort", "time"), Parameter.of("start", start), Parameter.of("mode", "list")).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Document document = getDocument(uri, false);
        List<Subject> subjects = new LinkedList<>();
        String listClass = ".list-view";
        for (Element li : document.selectFirst(listClass).select(HTML_LI)) {
            Element div = li.selectFirst(".title");
            Element a = div.selectFirst(">a");
            List<String> titles = Arrays.stream(a.text().strip().split("/")).map(String::strip).collect(Collectors.toList());
            Subject subject = new Subject();
            String href = a.attr("href");
            subject.setDbId(Long.parseLong(StringUtils.substringAfterLast(StringUtils.strip(href, "/"), "/")));
            subject.setTitle(titles.get(0));
            subject.setTagDate(LocalDate.parse(div.nextElementSibling().text().strip()));
            subject.setRecord(record);
            subjects.add(subject);
        }

        String numStr = document.selectFirst("span.subject-num").text().strip();
        String[] parts = StringUtils.split(numStr, "/- ");
        return new PageResult<>(Integer.parseInt(parts[0]) - 1,
                Integer.parseInt(parts[1]) - Integer.parseInt(parts[0]) + 1, Integer.parseInt(parts[2]), subjects);
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

    private <T> List<T> getApiObjects(String path) throws HttpResponseException {
        int start = 0;
        List<T> list = new LinkedList<>();
        while (true) {
            URIBuilder builder = super.buildUri(path, "api", Parameter.of("start", start));
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
        int start;
        int count;
        int total;
        List<T> data;

        public PageResult(int start, int count, int total, List<T> data) {
            this.start = start;
            this.count = count;
            this.total = total;
            this.data = data;
        }
    }
}
