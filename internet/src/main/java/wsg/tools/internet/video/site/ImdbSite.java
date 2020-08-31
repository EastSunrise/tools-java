package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RatedEnum;
import wsg.tools.internet.video.jackson.handler.SingletonListDeserializationProblemHandler;

import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * <a href="https://imdb.com">IMDb</a>
 *
 * @author Kingen
 * @since 2020/6/16
 */
public final class ImdbSite extends BaseSite {

    public static final String IMDB_TITLE_PREFIX = "tt";
    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("/title/(tt\\d+)/");

    public ImdbSite() {
        super("IMDb", "imdb.com", 1);
    }

    @Override
    protected void setObjectMapper() {
        super.setObjectMapper();
        objectMapper.registerModule(new SimpleModule()
                .addDeserializer(GenreEnum.class, EnumDeserializers.getTextDeserializer(GenreEnum.class))
                .addDeserializer(RatedEnum.class, EnumDeserializers.getAkaDeserializer(String.class, RatedEnum.class))
                .addDeserializer(LanguageEnum.class, EnumDeserializers.getAkaDeserializer(String.class, LanguageEnum.class))
        ).registerModule(new JavaTimeModule())
                .addHandler(SingletonListDeserializationProblemHandler.INSTANCE);
    }

    /**
     * Get subject info by parsing the html page
     */
    public BaseImdbTitle title(String tt) throws HttpResponseException {
        Document document;
        try {
            document = getDocument(buildPath("/title/%s", tt).build());
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }

        BaseImdbTitle subject;
        try {
            subject = objectMapper.readValue(document.selectFirst("script[type=application/ld+json]").html(), BaseImdbTitle.class);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
        return subject;
    }

    /**
     * Obtains ids of all episodes. Index of a given episode is array[currentSeason-1][currentEpisode].
     * Ep0 may be included if exists.
     */
    public String[][] episodes(String seriesId, int seasonsCount) throws HttpResponseException {
        String[][] result = new String[seasonsCount][];
        for (int index = 0; index < seasonsCount; index++) {
            Document document;
            try {
                document = getDocument(buildPath("/title/%s/episodes", seriesId)
                        .addParameter("season", String.valueOf(index + 1))
                        .build());
            } catch (URISyntaxException e) {
                throw AssertUtils.runtimeException(e);
            }

            Element element = document.selectFirst("meta[itemprop=numberofEpisodes]");
            int episodesCount = Integer.parseInt(element.attr("content"));
            String[] episodes = new String[episodesCount];

            Elements divs = document.select("div[itemprop=episodes]");
            for (Element div : divs) {
                String href = div.selectFirst(HTML_STRONG).selectFirst(HTML_A).attr(HTML_HREF).split("\\?")[0];
                String title = AssertUtils.matches(TITLE_HREF_REGEX, href).group(1);
                int episode = Integer.parseInt(div.selectFirst("meta[itemprop=episodeNumber]").attr("content"));
                if (episode == 0) {
                    continue;
                }
                episodes[episode] = title;
            }
            result[index] = episodes;
        }

        return result;
    }
}
