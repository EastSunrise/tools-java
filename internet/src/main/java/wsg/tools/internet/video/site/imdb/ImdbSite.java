package wsg.tools.internet.video.site.imdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.CssSelector;
import wsg.tools.internet.base.SiteStatus;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.base.exception.UnexpectedContentException;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RatingEnum;
import wsg.tools.internet.video.site.imdb.pojo.info.YearInfo;

import javax.annotation.Nonnull;
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
@SiteStatus(status = SiteStatus.Status.BLOCKED)
public final class ImdbSite extends BaseSite implements ImdbRepository<ImdbTitle> {

    private static final String TEXT_REGEX_STR = "[ \"!#%&'()*+,-./0-9:>?A-Za-z·\u0080-\u00FF]+";
    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("/title/(tt\\d+)/?");
    private static final Pattern MOVIE_PAGE_TITLE_REGEX = Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") (\\((?<year>\\d{4})\\) )?- IMDb");
    private static final Pattern SERIES_PAGE_TITLE_REGEX =
            Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") \\(TV (Mini-)?Series (?<start>\\d{4})(–((?<end>\\d{4})| )?)?\\) - IMDb");
    private static final Pattern SEASON_PAGE_TITLE_REGEX = Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") - Season (\\d{1,2}) - IMDb");
    private static final Pattern EPISODE_PAGE_TITLE_REGEX = Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") \\(TV Episode( (?<year>\\d{4}))?\\) - IMDb");
    private static final Pattern WORK_PAGE_TITLE_REGEX = Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") \\((Video )?(?<year>\\d{4})\\) - IMDb");
    private static final String EPISODES_PAGE_TITLE_SUFFIX = "- Episodes - IMDb";

    private static ImdbSite instance;

    private ImdbSite() {
        super("IMDb", "imdb.com");
    }

    public static ImdbSite getInstance() {
        if (instance == null) {
            instance = new ImdbSite();
        }
        return instance;
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

    @Override
    public ImdbTitle getItemById(@Nonnull String tt) throws NotFoundException {
        Document document = getDocument(builder0("/title/%s", tt), SnapshotStrategy.NEVER_UPDATE);
        ImdbTitle title;
        try {
            title = mapper.readValue(document.selectFirst("script[type=application/ld+json]").html(), ImdbTitle.class);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }

        title.setImdbId(tt);
        if (title instanceof ImdbEpisode) {
            String href = document.selectFirst("div.titleParent").selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF).split("\\?")[0];
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
            try {
                ((ImdbSeries) title).setEpisodes(getEpisodes(tt));
            } catch (NotFoundException e) {
                throw AssertUtils.runtimeException(e);
            }
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
                .filter(div -> div.selectFirst(CssSelector.TAG_H4) != null)
                .collect(Collectors.toMap(div -> StringUtils.strip(div.selectFirst(CssSelector.TAG_H4).text(), " :"), div -> div));
        Element block;
        final String language = "Language";
        if ((block = details.get(language)) != null) {
            List<LanguageEnum> languages = block.select(CssSelector.TAG_A).stream()
                    .map(a -> EnumUtilExt.deserializeAka(a.text(), LanguageEnum.class))
                    .collect(Collectors.toList());
            title.setLanguages(languages);
        }
        final String runtime = "Runtime";
        if ((block = details.get(runtime)) != null) {
            List<Duration> runtimes = block.select(CssSelector.TAG_TIME).stream()
                    .map(e -> Duration.parse(StringUtils.remove(e.attr(CssSelector.ATTR_DATETIME), ",")))
                    .collect(Collectors.toList());
            title.setRuntimes(runtimes);
        }

        return title;
    }

    private List<String[]> getEpisodes(String seriesId) throws NotFoundException {
        List<String[]> result = new ArrayList<>();
        int currentSeason = 0;
        while (true) {
            currentSeason++;
            Document document = getDocument(builder0("/title/%s/episodes", seriesId)
                    .addParameter("season", String.valueOf(currentSeason)), SnapshotStrategy.NEVER_UPDATE);
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
                String href = div.selectFirst(CssSelector.TAG_STRONG).selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF).split("\\?")[0];
                String id = RegexUtils.matchesOrElseThrow(TITLE_HREF_REGEX, href).group(1);
                int episode = Integer.parseInt(div.selectFirst("meta[itemprop=episodeNumber]").attr("content"));
                if (null != map.put(episode, id)) {
                    throw new UnexpectedContentException("Conflict episodes of " + seriesId);
                }
            }
            if (map.isEmpty()) {
                result.add(new String[1]);
                continue;
            }
            String[] episodes = new String[Collections.max(map.keySet()) + 1];
            map.forEach((key, value) -> episodes[key] = value);
            result.add(episodes);
        }
        return result;
    }
}
