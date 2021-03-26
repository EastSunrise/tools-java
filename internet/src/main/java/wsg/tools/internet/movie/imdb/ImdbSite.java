package wsg.tools.internet.movie.imdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.jackson.deserializer.AkaEnumDeserializer;
import wsg.tools.common.jackson.deserializer.TextEnumDeserializer;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.SiteStatus;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.RequestBuilder;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UnexpectedContentException;
import wsg.tools.internet.common.UnexpectedException;
import wsg.tools.internet.enums.Language;
import wsg.tools.internet.movie.common.RangeYear;
import wsg.tools.internet.movie.common.enums.ImdbRating;
import wsg.tools.internet.movie.common.enums.MovieGenre;

/**
 * <a href="https://imdb.com">IMDb</a>
 *
 * @author Kingen
 * @since 2020/6/16
 */
@SiteStatus(status = SiteStatus.Status.BLOCKED)
public final class ImdbSite extends BaseSite implements ImdbRepository<ImdbTitle> {

    private static final String TEXT_REGEX_STR = "[ \"!#%&'()*+,-./0-9:>?A-Za-z·\u0080-ÿ]+";
    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("/title/(tt\\d+)/?");
    private static final Pattern MOVIE_PAGE_TITLE_REGEX =
        Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") (\\((?<year>\\d{4})\\) )?- IMDb");
    private static final Pattern SERIES_PAGE_TITLE_REGEX = Pattern
        .compile("(?<text>" + TEXT_REGEX_STR
            + ") \\(TV (Mini-)?Series (?<s>\\d{4})(–((?<e>\\d{4})| )?)?\\) - IMDb");
    private static final Pattern SEASON_PAGE_TITLE_REGEX =
        Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") - Season (\\d{1,2}) - IMDb");
    private static final Pattern EPISODE_PAGE_TITLE_REGEX =
        Pattern
            .compile("(?<text>" + TEXT_REGEX_STR + ") \\(TV Episode( (?<year>\\d{4}))?\\) - IMDb");
    private static final Pattern WORK_PAGE_TITLE_REGEX =
        Pattern.compile("(?<text>" + TEXT_REGEX_STR + ") \\((Video )?(?<year>\\d{4})\\) - IMDb");
    private static final String EPISODES_PAGE_TITLE_SUFFIX = "- Episodes - IMDb";
    private static final ObjectMapper MAPPER =
        new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .setLocale(Locale.ENGLISH)
            .registerModule(new SimpleModule()
                .addDeserializer(MovieGenre.class, new TextEnumDeserializer<>(MovieGenre.class))
                .addDeserializer(ImdbRating.class,
                    new AkaEnumDeserializer<>(String.class, ImdbRating.class))
                .addDeserializer(Language.class,
                    new AkaEnumDeserializer<>(String.class, Language.class)))
            .registerModule(new JavaTimeModule());

    public ImdbSite() {
        super("IMDb", new BasicHttpSession("imdb.com"));
    }

    @Nonnull
    @Override
    public ImdbTitle findById(String tt) throws NotFoundException, OtherResponseException {
        Objects.requireNonNull(tt);
        Document document = getDocument(builder0("/title/%s", tt), SnapshotStrategy.never());
        ImdbTitle title;
        try {
            String html = document.selectFirst("script[type=application/ld+json]").html();
            title = MAPPER.readValue(html, ImdbTitle.class);
        } catch (JsonProcessingException e) {
            throw new UnexpectedException(e);
        }

        title.setImdbId(tt);
        if (title instanceof ImdbEpisode) {
            String href = document.selectFirst("div.titleParent").selectFirst(CssSelectors.TAG_A)
                .attr(CssSelectors.ATTR_HREF).split("\\?")[0];
            String seriesId = RegexUtils.matchesOrElseThrow(TITLE_HREF_REGEX, href).group(1);
            ((ImdbEpisode) title).setSeriesId(seriesId);
        }

        if (title instanceof ImdbMovie) {
            Matcher matcher = RegexUtils
                .matchesOrElseThrow(MOVIE_PAGE_TITLE_REGEX, document.title());
            String year = matcher.group("year");
            if (year != null) {
                ((ImdbMovie) title).setYear(Integer.parseInt(year));
            }
        } else if (title instanceof ImdbSeries) {
            Matcher matcher = RegexUtils
                .matchesOrElseThrow(SERIES_PAGE_TITLE_REGEX, document.title());
            String end = matcher.group("end");
            ((ImdbSeries) title).setRangeYear(
                new RangeYear(Integer.parseInt(matcher.group("start")),
                    end == null ? null : Integer.parseInt(end)));
            ((ImdbSeries) title).setEpisodes(getEpisodes(tt));
        } else if (title instanceof ImdbEpisode) {
            Matcher matcher = RegexUtils
                .matchesOrElseThrow(EPISODE_PAGE_TITLE_REGEX, document.title());
            String year = matcher.group("year");
            if (year != null) {
                ((ImdbEpisode) title).setYear(Integer.parseInt(year));
            }
        } else if (title instanceof ImdbCreativeWork) {
            Matcher matcher = RegexUtils
                .matchesOrElseThrow(WORK_PAGE_TITLE_REGEX, document.title());
            ((ImdbCreativeWork) title).setYear(Integer.parseInt(matcher.group("year")));
        } else {
            throw new UnexpectedContentException("Unknown type of imdb title: " + tt);
        }

        Map<String,
            Element> details = document.selectFirst("div#titleDetails").select("div.txt-block")
            .stream()
            .filter(div -> div.selectFirst(CssSelectors.TAG_H4) != null).collect(Collectors
                .toMap(div -> StringUtils.strip(div.selectFirst(CssSelectors.TAG_H4).text(), " :"),
                    div -> div));
        Element block;
        block = details.get("Language");
        if (null != block) {
            List<Language> languages = block.select(CssSelectors.TAG_A).stream()
                .map(a -> EnumUtilExt.deserializeAka(a.text(), Language.class))
                .collect(Collectors.toList());
            title.setLanguages(languages);
        }
        block = details.get("Runtime");
        if (null != block) {
            List<Duration> runtimes = block.select(CssSelectors.TAG_TIME).stream()
                .map(
                    e -> Duration
                        .parse(StringUtils.remove(e.attr(CssSelectors.ATTR_DATETIME), ",")))
                .collect(Collectors.toList());
            title.setRuntimes(runtimes);
        }

        return title;
    }

    private List<String[]> getEpisodes(String seriesId)
        throws NotFoundException, OtherResponseException {
        List<String[]> result = new ArrayList<>();
        int currentSeason = 0;
        while (true) {
            currentSeason++;
            RequestBuilder builder = builder0("/title/%s/episodes", seriesId)
                .addParameter("season", currentSeason);
            Document document = getDocument(builder, SnapshotStrategy.never());
            String title = document.title();
            if (title.endsWith(EPISODES_PAGE_TITLE_SUFFIX)) {
                break;
            }
            Matcher matcher = RegexUtils.matchesOrElseThrow(SEASON_PAGE_TITLE_REGEX, title);
            if (Integer.parseInt(matcher.group(2)) != currentSeason) {
                break;
            }

            Element element = document.selectFirst("meta[itemprop=numberofEpisodes]");
            int episodesCount = Integer.parseInt(element.attr(CssSelectors.ATTR_CONTENT));
            Elements divs = document.select("div[itemprop=episodes]");
            Map<Integer, String> map = new HashMap<>(episodesCount);
            for (int i = divs.size() - 1; i >= 0; i--) {
                Element div = divs.get(i);
                String href = div.selectFirst(CssSelectors.TAG_STRONG)
                    .selectFirst(CssSelectors.TAG_A)
                    .attr(CssSelectors.ATTR_HREF).split("\\?")[0];
                String id = RegexUtils.matchesOrElseThrow(TITLE_HREF_REGEX, href).group(1);
                int episode = Integer
                    .parseInt(div.selectFirst("meta[itemprop=episodeNumber]")
                        .attr(CssSelectors.ATTR_CONTENT));
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
