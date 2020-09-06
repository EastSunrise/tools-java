package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.entity.imdb.object.ImdbEpisode;
import wsg.tools.internet.video.entity.imdb.object.ImdbSeries;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RatingEnum;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a href="https://imdb.com">IMDb</a>
 *
 * @author Kingen
 * @since 2020/6/16
 */
public final class ImdbSite extends BaseSite {

    public static final String IMDB_TITLE_PREFIX = "tt";
    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("/title/(tt\\d+)/?");
    private static final Pattern SEASON_EPISODES_REGEX = Pattern.compile("/title/tt\\d+/episodes\\?season=\\d+");

    public ImdbSite() {
        super("IMDb", "imdb.com", 1);
    }

    @Override
    protected ObjectMapper objectMapper() {
        return super.objectMapper()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .setLocale(Locale.ENGLISH)
                .registerModule(new SimpleModule()
                        .addDeserializer(GenreEnum.class, EnumDeserializers.getTextDeserializer(GenreEnum.class))
                        .addDeserializer(RatingEnum.class, EnumDeserializers.getAkaDeserializer(String.class, RatingEnum.class))
                        .addDeserializer(LanguageEnum.class, EnumDeserializers.getAkaDeserializer(String.class, LanguageEnum.class))
                ).registerModule(new JavaTimeModule());
    }

    /**
     * Get subject info by parsing the html page
     */
    public BaseImdbTitle title(String tt) throws HttpResponseException {
        Document document = getDocument(uriBuilder("/title/%s", tt));
        BaseImdbTitle subject;
        try {
            subject = objectMapper.readValue(document.selectFirst("script[type=application/ld+json]").html(), BaseImdbTitle.class);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
        if (subject instanceof ImdbEpisode) {
            ImdbEpisode episode = (ImdbEpisode) subject;
            String href = document.selectFirst("div.titleParent").selectFirst(HTML_A).attr(HTML_HREF).split("\\?")[0];
            String seriesId = AssertUtils.matches(TITLE_HREF_REGEX, href).group(1);
            episode.setSeriesId(seriesId);
            return episode;
        }
        if (subject instanceof ImdbSeries) {
            ImdbSeries series = (ImdbSeries) subject;
            Element widget = document.selectFirst("div#title-episode-widget");
            if (widget != null) {
                Matcher matcher = SEASON_EPISODES_REGEX.matcher(widget.html());
                int seasonsCount = 0;
                while (matcher.find()) {
                    seasonsCount++;
                }
                if (seasonsCount != 0) {
                    series.setSeasonsCount(seasonsCount);
                }
            }
            return series;
        }
        return subject;
    }

    /**
     * Obtains ids of all episodes. Index of a given episode is array[currentSeason-1][currentEpisode].
     * Ep0 may be included if exists.
     */
    @Nullable
    public String[][] episodes(String seriesId, int seasonsCount) throws HttpResponseException {
        String[][] result = new String[seasonsCount][];
        for (int index = 0; index < seasonsCount; index++) {
            Document document = getDocument(uriBuilder("/title/%s/episodes", seriesId)
                    .addParameter("season", String.valueOf(index + 1)));
            Element element = document.selectFirst("meta[itemprop=numberofEpisodes]");
            if (element == null) {
                return null;
            }
            int episodesCount = Integer.parseInt(element.attr("content"));

            Elements divs = document.select("div[itemprop=episodes]");
            Map<Integer, String> map = new HashMap<>(episodesCount);
            for (int i = divs.size() - 1; i >= 0; i--) {
                Element div = divs.get(i);
                String href = div.selectFirst(HTML_STRONG).selectFirst(HTML_A).attr(HTML_HREF).split("\\?")[0];
                String title = AssertUtils.matches(TITLE_HREF_REGEX, href).group(1);
                int episode = Integer.parseInt(div.selectFirst("meta[itemprop=episodeNumber]").attr("content"));
                if (null != map.put(episode, title)) {
                    throw new HttpResponseException(HttpStatus.SC_EXPECTATION_FAILED, "Conflict episodes of " + seriesId);
                }
            }
            String[] episodes = new String[Collections.max(map.keySet()) + 1];
            map.forEach((key, value) -> episodes[key] = value);
            result[index] = episodes;
        }

        return result;
    }
}
