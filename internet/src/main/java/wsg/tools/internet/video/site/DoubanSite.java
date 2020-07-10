package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.video.entity.Celebrity;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.enums.*;
import wsg.tools.internet.video.jackson.deserializer.DurationExtDeserializer;
import wsg.tools.internet.video.jackson.deserializer.PubDateDeserializer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
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
    private static final String DELIMITER = "/";
    private static final PubDateDeserializer PUB_DATE_DESERIALIZER = new PubDateDeserializer();
    private static final DurationExtDeserializer DURATION_DESERIALIZER = new DurationExtDeserializer();
    private static final Pattern IMDB_REGEX = Pattern.compile("https://www.imdb.com/title/(tt\\d{7,})");

    private String apiKey;

    public DoubanSite(String apiKey) {
        super("Douban", "douban.com", 1);
        this.apiKey = apiKey;
    }

    /**
     * Collect user movies data since startDate.
     */
    public List<Subject> collectUserMovies(long userId, LocalDate startDate) throws IOException, URISyntaxException {
        LinkedList<Subject> subjects = new LinkedList<>();
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
        return subjects;
    }

    /**
     * Obtains movie subject through api, with id of IMDb acquired by parsing the web page of the subject.
     * <p>
     * This method can't obtain x-rated subjects probably which are restricted to be accessed only after logging in.
     */
    public Subject movieSubject(long dbId) throws URISyntaxException, IOException {
        Subject subject = getApiObject(String.format("/v2/movie/subject/%d", dbId), Subject.class);
        subject.setImdbId(parseSubjectPage(dbId).getImdbId());
        return subject;
    }

    public Celebrity apiMovieCelebrity(long id) throws IOException, URISyntaxException {
        return getApiObject(String.format("/v2/movie/celebrity/%d", id), Celebrity.class);
    }

    public List<Subject> apiMovieTop250() throws IOException, URISyntaxException {
        return getApiObjects("/v2/movie/top250");
    }

    public List<Subject> apiMovieWeekly() throws IOException, URISyntaxException {
        return getApiObjects("/v2/movie/weekly");
    }

    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = super.getObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return objectMapper;
    }

    /**
     * Parse pages of subjects.
     * <p>
     * This is supplement of {@link #movieSubject(long)} (long)} to get IMDb identity of a subject.
     * <p>
     * Same as {@link #movieSubject(long)} (long)}, this method can't obtain x-rated subject probably.
     */
    private Subject parseSubjectPage(long dbId) throws IOException, URISyntaxException {
        Document document = getDocument(buildUri("/subject/" + dbId, "movie").build());
        Subject subject = new Subject();
        subject.setDbId(dbId);
        Element wrapper = document.getElementById("wrapper");

        Elements spans = wrapper.selectFirst("div#info").select("span.pl");
        Map<String, Element> spanMap = spans.stream().collect(Collectors.toMap(span -> span.text().strip(), span -> span));
        Element span;

        Element dateEle = spanMap.get("上映日期:");
        if (dateEle != null) {
            subject.setSubtype(SubtypeEnum.MOVIE);
            span = spanMap.get("片长:");
            Set<Duration> durations = new HashSet<>();
            if ((span = span.nextElementSibling()).is("span[property=v:runtime]")) {
                durations.add(Duration.ofMinutes(Integer.parseInt(span.attr("content"))));
            }
            Node node = nextSibling(span);
            if (node instanceof TextNode) {
                String[] contents = StringUtils.strip(node.toString(), " /").split("/");
                for (String content : contents) {
                    durations.add(DURATION_DESERIALIZER.toNonNullT(content.strip()));
                }
            }
            subject.setDurations(durations);
        } else if ((dateEle = spanMap.get("首播:")) != null) {
            subject.setSubtype(SubtypeEnum.TV);
            span = spanMap.get("单集片长:");
            if (span != null) {
                Set<Duration> durations = new HashSet<>();
                String[] contents = StringUtils.strip(nextSiblingString(span), " /").split("/");
                for (String content : contents) {
                    durations.add(DURATION_DESERIALIZER.toNonNullT(content.strip()));
                }
                subject.setDurations(durations);
            }
            subject.setEpisodesCount(Integer.parseInt(nextSiblingString(spanMap.get("集数:"))));
            span = spanMap.get("季数:");
            if (span != null) {
                Node node = nextSibling(span);
                if (node instanceof TextNode) {
                    subject.setCurrentSeason(Integer.parseInt(node.toString().strip()));
                } else if (node instanceof Element) {
                    Element element = (Element) node;
                    if (HTML_SELECT.equals((element).tagName())) {
                        subject.setCurrentSeason(Integer.parseInt(element.selectFirst("option[selected=selected]").text().strip()));
                        subject.setSeasonsCount(element.select("option").size());
                    }
                }
            }
        } else {
            subject.setSubtype(null);
        }

        // title and original title
        String[] keywords = document.selectFirst("meta[name=keywords]").attr("content").split(",");
        subject.setTitle(keywords[0].strip());
        subject.setOriginalTitle(keywords[1].strip());

        // year
        String yearStr = StringUtils.strip(wrapper.selectFirst("h1").selectFirst("span.year").text(), "( )");
        subject.setYear(Year.of(Integer.parseInt(yearStr)));

        span = spanMap.get("类型:");
        if (span != null) {
            List<GenreEnum> genres = new LinkedList<>();
            Element genre = span.nextElementSibling();
            while (genre.is("span[property=v:genre]")) {
                genres.add(EnumUtilExt.deserializeTitle(genre.text().strip(), GenreEnum.class));
                genre = genre.nextElementSibling();
            }
            subject.setGenres(genres);
        }

        span = spanMap.get("制片国家/地区:");
        Set<Country> countries = Arrays.stream(nextSiblingString(span).split(DELIMITER))
                .map(part -> Country.ofTitle(part.strip())).collect(Collectors.toSet());
        subject.setCountries(countries);

        span = spanMap.get("语言:");
        Set<Language> languages = Arrays.stream(nextSiblingString(span).split(DELIMITER))
                .map(part -> Language.ofTitle(part.strip())).collect(Collectors.toSet());
        subject.setLanguages(languages);

        span = spanMap.get("又名:");
        if (span != null) {
            List<String> aka = Arrays.stream(nextSiblingString(span).split(DELIMITER))
                    .map(String::strip).collect(Collectors.toList());
            subject.setAka(aka);
        }

        if (dateEle != null) {
            Set<LocalDate> dates = new HashSet<>();
            dateEle = dateEle.nextElementSibling();
            while (dateEle.is("span[property=v:initialReleaseDate]")) {
                String content = dateEle.attr("content");
                dates.add(PUB_DATE_DESERIALIZER.toNonNullT(content));
                dateEle = dateEle.nextElementSibling();
            }
            subject.setPubDates(dates);
        }

        span = spanMap.get("官方网站:");
        if (span != null) {
            if ((span = span.nextElementSibling()).is(HTML_A)) {
                subject.setWebsite(span.attr("href"));
            }
        }

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
     * @param record  wish/do/collect
     * @param start   start index
     */
    private PageResult<Subject> parseCollectionsPage(long userId, CatalogEnum catalog, RecordEnum record, int start) throws URISyntaxException, IOException {
        URI uri = buildUri(String.format("/people/%d/%s", userId, record.name().toLowerCase()), catalog.name().toLowerCase(),
                Parameter.of("sort", "time"), Parameter.of("start", start), Parameter.of("mode", "list")).build();
        Document document = getDocument(uri, false);
        List<Subject> subjects = new LinkedList<>();
        for (Element li : document.selectFirst(".list-view").select(HTML_LI)) {
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

    /**
     * Get next sibling node which isn't an empty string
     */
    private Node nextSibling(Node node) {
        Objects.requireNonNull(node);
        Node next = node.nextSibling();
        while ("".equals(next.toString().strip())) {
            next = next.nextSibling();
        }
        return next;
    }

    private String nextSiblingString(Node node) {
        return nextSibling(node).toString().strip();
    }

    private <T> T getApiObject(String path, Class<T> clazz, Parameter... params) throws URISyntaxException, IOException {
        URIBuilder builder = super.buildUri(path, "api", params);
        builder.addParameter("apikey", apiKey);
        return getObject(builder.build(), clazz);
    }

    private <T> List<T> getApiObjects(String path) throws URISyntaxException, IOException {
        int start = 0;
        List<T> list = new LinkedList<>();
        while (true) {
            URIBuilder builder = super.buildUri(path, "api", Parameter.of("start", start));
            builder.addParameter("apikey", apiKey);
            PageResult<T> pageResult = getObject(builder.build(), new TypeReference<>() {});
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
