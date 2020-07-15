package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.enums.SubtypeEnum;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a href="https://imdb.com">IMDb</a>
 *
 * @author Kingen
 * @since 2020/6/16
 */
public class ImdbSite extends AbstractVideoSite {

    private static final Pattern MOVIE_TITLE_REGEX = Pattern.compile("([^()]+) \\((TV Movie )?(\\d{4})\\) - IMDb");
    private static final Pattern TV_TITLE_REGEX = Pattern.compile("([^()]+) \\(TV ((Mini-)?Series|Episode) (\\d{4})(–(\\d{4}|\\s))?\\) - IMDb");
    private static final Pattern VIDEO_TITLE_REGEX = Pattern.compile("([^()]+) \\((Video )?(\\d{4})\\) - IMDb");

    public ImdbSite() {
        super("IMDb", "imdb.com", 1);
    }

    /**
     * Get subject info by parsing the html page
     */
    public Subject title(String tt) throws HttpResponseException {
        Document document;
        try {
            document = getDocument(buildUri("/title/" + tt, null).build());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Subject subject = new Subject();
        subject.setImdbId(tt);

        Element head = document.selectFirst("head");
        JsonNode root;
        try {
            root = getObjectMapper().readTree(head.selectFirst("script[type=application/ld+json]").html());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
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

        JsonNode datePublished = root.get("datePublished");
        if (datePublished != null) {
            subject.setReleases(List.of(LocalDate.parse(datePublished.textValue())));
        }
        Map<String, Element> details = new HashMap<>(10);
        String detailsId = "#titleDetails", blockClass = ".txt-block";
        for (Element element : document.selectFirst(detailsId).select(blockClass)) {
            Element h4 = element.selectFirst("h4");
            if (h4 != null) {
                details.put(h4.text(), element);
            }
        }
        if (details.isEmpty()) {
            return subject;
        }

        return subject;
    }
}
