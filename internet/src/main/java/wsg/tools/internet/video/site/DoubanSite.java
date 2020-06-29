package wsg.tools.internet.video.site;

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
import wsg.tools.common.util.CodecUtils;
import wsg.tools.common.util.EnumUtils;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.enums.Country;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.Language;
import wsg.tools.internet.video.enums.SubtypeEnum;
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
 * <a href="https://douban.com">豆瓣</a>
 * <p>
 * An api key is required.
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
public class DoubanSite extends AbstractVideoSite {

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
     * Get movie subject by parsing html.
     */
    public Subject movieSubject(long id) throws IOException, URISyntaxException {
        Document document = getDocument(buildUri("/subject/" + id, "movie").build());
        Subject subject = new Subject();
        subject.setId(id);
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
            while (hasProperty(genre, "genre")) {
                genres.add(EnumUtils.deserializeTitle(genre.text().strip(), GenreEnum.class));
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
            while (hasProperty(dateEle, "initialReleaseDate")) {
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

    private boolean hasProperty(Element element, String property) {
        return element.is(String.format("span[property=v:%s]", property));
    }

    /**
     * Get movie subject through api
     */
    public Subject apiMovieSubject(long id) throws URISyntaxException, IOException {
        return getApiObject(String.format("/v2/movie/subject/%d", id), Subject.class);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = super.getObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return objectMapper;
    }

    /**
     * Get next sibling node which isn't an empty string
     */
    private Node nextSibling(Node node) {
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

    @Override
    protected String getContent(URI uri) throws IOException {
        return CodecUtils.unicodeDecode(super.getContent(uri));
    }
}
