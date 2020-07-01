package wsg.tools.internet.video.site;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.enums.Country;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.Language;
import wsg.tools.internet.video.enums.SubtypeEnum;
import wsg.tools.internet.video.jackson.deserializer.DurationExtDeserializer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <a href="https://imdb.com">IMDb</a>
 *
 * @author Kingen
 * @since 2020/6/16
 */
public class ImdbSite extends AbstractVideoSite {

    private static final DurationExtDeserializer DURATION_DESERIALIZER = new DurationExtDeserializer();
    private static final Pattern MOVIE_TITLE_REGEX = Pattern.compile("([^()]+) \\((TV Movie )?(\\d{4})\\) - IMDb");
    private static final Pattern TV_TITLE_REGEX = Pattern.compile("([^()]+) \\(TV ((Mini-)?Series|Episode) (\\d{4})(–(\\d{4}|\\s))?\\) - IMDb");
    private static final Pattern VIDEO_TITLE_REGEX = Pattern.compile("([^()]+) \\((Video )?(\\d{4})\\) - IMDb");

    public ImdbSite() {
        super("IMDb", "imdb.com", 1);
    }

    /**
     * Get subject info by parsing the html page
     */
    public Subject title(String tt) throws IOException, URISyntaxException {
        Document document = getDocument(buildUri("/title/" + tt, null).build());
        Subject subject = new Subject();
        subject.setImdbId(tt);

        Element head = document.selectFirst("head");
        JsonNode root = getObjectMapper().readTree(head.selectFirst("script[type=application/ld+json]").html());
        Map<String, Element> details = new HashMap<>(10);
        for (Element element : document.selectFirst("#titleDetails").select(".txt-block")) {
            Element h4 = element.selectFirst("h4");
            if (h4 != null) {
                details.put(h4.text(), element);
            }
        }

        SubtypeEnum type = EnumUtilExt.deserializeAka(root.get("@type").textValue(), SubtypeEnum.class);
        subject.setSubtype(type);
        String yearStr;
        if (SubtypeEnum.MOVIE.equals(type)) {
            Matcher matcher = AssertUtils.matches(MOVIE_TITLE_REGEX, head.selectFirst("title").text());
            yearStr = matcher.group(3);
        } else if (SubtypeEnum.TV_SERIES.equals(type) || SubtypeEnum.TV_EPISODE.equals(type)) {
            Matcher matcher = AssertUtils.matches(TV_TITLE_REGEX, head.selectFirst("title").text());
            yearStr = matcher.group(4);
        } else if (SubtypeEnum.CREATIVE_WORK.equals(type)) {
            Matcher matcher = AssertUtils.matches(VIDEO_TITLE_REGEX, head.selectFirst("title").text());
            yearStr = matcher.group(3);
        } else {
            throw new RuntimeException("Unknown subtype " + type);
        }
        subject.setYear(Year.of(Integer.parseInt(yearStr)));
        subject.setText(root.get("name").textValue());

        JsonNode genres = root.get("genre");
        if (genres != null) {
            List<GenreEnum> genreList = new LinkedList<>();
            if (JsonNodeType.ARRAY.equals(genres.getNodeType())) {
                ArrayNode nodes = (ArrayNode) genres;
                for (Iterator<JsonNode> iterator = nodes.elements(); iterator.hasNext(); ) {
                    genreList.add(EnumUtilExt.deserializeText(iterator.next().textValue(), GenreEnum.class));
                }
            } else {
                genreList.add(EnumUtilExt.deserializeText(genres.textValue(), GenreEnum.class));
            }
            subject.setGenres(genreList);
        }

        Element detail;
        if ((detail = details.get("Runtime:")) != null) {
            Set<Duration> durations = new HashSet<>();
            for (Element time : detail.select("time[datetime]")) {
                durations.add(DURATION_DESERIALIZER.toNonNullT(time.attr("datetime")));
            }
            subject.setDurations(durations);
        }

        if ((detail = details.get("Country:")) != null) {
            Set<Country> countries = detail.select("a").stream()
                    .map(a -> Country.ofText(a.text().strip())).collect(Collectors.toSet());
            subject.setCountries(countries);
        }

        if ((detail = details.get("Language:")) != null) {
            Set<Language> languages = detail.select("a").stream()
                    .map(a -> Language.ofText(a.text().strip())).collect(Collectors.toSet());
            subject.setLanguages(languages);
        }

        if ((detail = details.get("Official Sites:")) != null) {
            if ((detail = detail.nextElementSibling()).is("a")) {
                subject.setWebsite(buildUri(detail.attr("href"), null).toString());
            }
        }

        JsonNode datePublished = root.get("datePublished");
        if (datePublished != null) {
            subject.setPubDates(Set.of(LocalDate.parse(datePublished.textValue())));
        }

        return subject;
    }
}
