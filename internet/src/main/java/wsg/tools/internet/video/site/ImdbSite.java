package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.base.exception.UnexpectedContentException;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.entity.imdb.info.YearInfo;
import wsg.tools.internet.video.entity.imdb.object.ImdbCreativeWork;
import wsg.tools.internet.video.entity.imdb.object.ImdbEpisode;
import wsg.tools.internet.video.entity.imdb.object.ImdbMovie;
import wsg.tools.internet.video.entity.imdb.object.ImdbSeries;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RatingEnum;

import java.time.Duration;
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
public final class ImdbSite extends BaseSite {

    private static final String TEXT_REGEX_STR = "[ \"!#%&'()*+,-./0-9:>?A-z·\u0080-\u00FF]+";
    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("/title/(tt\\d+)/?");
    private static final Pattern MOVIE_PAGE_TITLE_REGEX =
            Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") (\\((?<year>\\d{4})\\) )?- IMDb");
    private static final Pattern SERIES_PAGE_TITLE_REGEX =
            Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") \\(TV (Mini-)?Series (?<start>\\d{4})(–((?<end>\\d{4})| )?)?\\) - IMDb");
    private static final Pattern SEASON_PAGE_TITLE_REGEX =
            Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") - Season (\\d{1,2}) - IMDb");
    private static final Pattern EPISODE_PAGE_TITLE_REGEX =
            Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") \\(TV Episode( (?<year>\\d{4}))?\\) - IMDb");
    private static final Pattern WORK_PAGE_TITLE_REGEX =
            Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") \\((Video )?(?<year>\\d{4})\\) - IMDb");
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
    public BaseImdbTitle title(String tt) throws NotFoundException {
        Document document = getDocument(builder0("/title/%s", tt), true);
        BaseImdbTitle title;
        try {
            title = mapper.readValue(document.selectFirst("script[type=application/ld+json]").html(), BaseImdbTitle.class);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }

        if (title instanceof ImdbEpisode) {
            String href = document.selectFirst("div.titleParent").selectFirst(TAG_A).attr(ATTR_HREF).split("\\?")[0];
            String seriesId = RegexUtils.matchesOrElseThrow(TITLE_HREF_REGEX, href).group(1);
            ((ImdbEpisode) title).setSeriesId(seriesId);
        }

        if (title instanceof ImdbMovie) {
            Matcher matcher = RegexUtils.matchesOrElseThrow(MOVIE_PAGE_TITLE_REGEX, document.title());
            String year = matcher.group("year");
            if (year != null) {
                ((ImdbMovie) title).setYear(Year.of(Integer.parseInt(year)));
            }
        } else if (title instanceof ImdbSeries) {
            Matcher matcher = RegexUtils.matchesOrElseThrow(SERIES_PAGE_TITLE_REGEX, document.title());
            String end = matcher.group("end");
            ((ImdbSeries) title).setYearInfo(new YearInfo(
                    Year.of(Integer.parseInt(matcher.group("start"))),
                    end == null ? null : Year.of(Integer.parseInt(end))
            ));
        } else if (title instanceof ImdbEpisode) {
            Matcher matcher = RegexUtils.matchesOrElseThrow(EPISODE_PAGE_TITLE_REGEX, document.title());
            String year = matcher.group("year");
            if (year != null) {
                ((ImdbEpisode) title).setYear(Year.of(Integer.parseInt(year)));
            }
        } else if (title instanceof ImdbCreativeWork) {
            Matcher matcher = RegexUtils.matchesOrElseThrow(WORK_PAGE_TITLE_REGEX, document.title());
            ((ImdbCreativeWork) title).setYear(Year.of(Integer.parseInt(matcher.group("year"))));
        } else {
            throw new UnexpectedContentException("Unknown type of imdb title: " + tt);
        }

        Map<String, Element> details = document.selectFirst("div#titleDetails").select("div.txt-block").stream()
                .filter(div -> div.selectFirst(TAG_H4) != null)
                .collect(Collectors.toMap(div -> StringUtils.strip(div.selectFirst(TAG_H4).text(), " :"), div -> div));
        Element block;
        final String language = "Language";
        if ((block = details.get(language)) != null) {
            List<LanguageEnum> languages = block.select(TAG_A).stream()
                    .map(a -> EnumUtilExt.deserializeAka(a.text(), LanguageEnum.class))
                    .collect(Collectors.toList());
            title.setLanguages(languages);
        }
        final String runtime = "Runtime";
        if ((block = details.get(runtime)) != null) {
            List<Duration> runtimes = block.select(TAG_TIME).stream()
                    .map(e -> Duration.parse(StringUtils.remove(e.attr(ATTR_DATETIME), SignEnum.COMMA.getC())))
                    .collect(Collectors.toList());
            title.setRuntimes(runtimes);
        }

        return title;
    }

    /**
     * Obtains ids of all episodes. Index of a given episode is array[currentSeason-1][currentEpisode].
     * Ep0 may be included if exists.
     *
     * @return all episodes. Maybe uncompleted.
     */
    public List<String[]> episodes(String seriesId) throws NotFoundException {
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
            Matcher matcher = RegexUtils.matchesOrElseThrow(SEASON_PAGE_TITLE_REGEX, title);
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
                String id = RegexUtils.matchesOrElseThrow(TITLE_HREF_REGEX, href).group(1);
                int episode = Integer.parseInt(div.selectFirst("meta[itemprop=episodeNumber]").attr("content"));
                if (null != map.put(episode, id)) {
                    throw new UnexpectedContentException("Conflict episodes of " + seriesId);
                }
            }
            String[] episodes = new String[Collections.max(map.keySet()) + 1];
            map.forEach((key, value) -> episodes[key] = value);
            result.add(episodes);
        }
        return result;
    }
}
