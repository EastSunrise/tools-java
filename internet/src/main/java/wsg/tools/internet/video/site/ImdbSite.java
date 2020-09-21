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
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RatingEnum;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a href="https://imdb.com">IMDb</a>
 *
 * @author Kingen
 * @since 2020/6/16
 */
public final class ImdbSite extends BaseSite<String> {

    private static final String TEXT_REGEX_STR = "[ !#%&'()*+,-./0-9:>?A-z·áâèéñóôùûü]+";
    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("/title/(tt\\d+)/?");
    private static final Pattern SEASON_PAGE_TITLE_REGEX = Pattern.compile("(" + TEXT_REGEX_STR + ") - Season (\\d{1,2}) - IMDb");
    private static final String EPISODES_PAGE_TITLE_SUFFIX = "- Episodes - IMDb";

    public ImdbSite() {
        super("IMDb", "imdb.com", 10);
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
    public BaseImdbTitle title(String tt) throws IOException {
        Document document = getDocument(builder0("/title/%s", tt), true);
        BaseImdbTitle subject;
        try {
            subject = mapper.readValue(document.selectFirst("script[type=application/ld+json]").html(), BaseImdbTitle.class);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
        if (subject instanceof ImdbEpisode) {
            ImdbEpisode episode = (ImdbEpisode) subject;
            String href = document.selectFirst("div.titleParent").selectFirst(TAG_A).attr(ATTR_HREF).split("\\?")[0];
            String seriesId = AssertUtils.matches(TITLE_HREF_REGEX, href).group(1);
            episode.setSeriesId(seriesId);
            return episode;
        }
        return subject;
    }

    /**
     * Obtains ids of all episodes. Index of a given episode is array[currentSeason-1][currentEpisode].
     * Ep0 may be included if exists.
     *
     * @return all episodes. Maybe uncompleted.
     * @throws IOException 404 if not TV series, or other HTTP errors.
     */
    public List<String[]> episodes(String seriesId) throws IOException {
        List<String[]> result = new ArrayList<>();
        int currentSeason = 0;
        while (true) {
            currentSeason++;
            Document document = getDocument(builder0("/title/%s/episodes", seriesId)
                    .addParameter("season", String.valueOf(currentSeason)), true);
            String title = document.title();
            if (title.endsWith(EPISODES_PAGE_TITLE_SUFFIX)) {
                break;
            }
            Matcher matcher = AssertUtils.matches(SEASON_PAGE_TITLE_REGEX, title);
            if (Integer.parseInt(matcher.group(2)) != currentSeason) {
                break;
            }

            Element element = document.selectFirst("meta[itemprop=numberofEpisodes]");
            int episodesCount = Integer.parseInt(element.attr("content"));
            Elements divs = document.select("div[itemprop=episodes]");
            Map<Integer, String> map = new HashMap<>(episodesCount);
            for (int i = divs.size() - 1; i >= 0; i--) {
                Element div = divs.get(i);
                String href = div.selectFirst(TAG_STRONG).selectFirst(TAG_A).attr(ATTR_HREF).split("\\?")[0];
                String id = AssertUtils.matches(TITLE_HREF_REGEX, href).group(1);
                int episode = Integer.parseInt(div.selectFirst("meta[itemprop=episodeNumber]").attr("content"));
                if (null != map.put(episode, id)) {
                    throw new HttpResponseException(HttpStatus.SC_EXPECTATION_FAILED, "Conflict episodes of " + seriesId);
                }
            }
            String[] episodes = new String[Collections.max(map.keySet()) + 1];
            map.forEach((key, value) -> episodes[key] = value);
            result.add(episodes);
        }
        return result;
    }
}
