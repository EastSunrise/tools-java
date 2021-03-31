package wsg.tools.internet.movie.imdb;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.RequestBuilder;
import wsg.tools.internet.base.support.SnapshotStrategies;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.StringResponseHandler;
import wsg.tools.internet.movie.common.RangeYear;
import wsg.tools.internet.movie.common.Release;

/**
 * <a href="https://www.imdb.cn">IMDb CN</a>
 *
 * @author Kingen
 * @since 2020/12/12
 */
@ConcreteSite
public final class ImdbCnSite extends BaseSite implements ImdbRepository<ImdbTitle> {

    private static final Pattern EPISODE_LIST_REGEX = Pattern
        .compile("/title/(?<id>tt\\d+)/episodelist\\?season=\\d+");
    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("/title/(?<id>tt\\d+)/");
    private static final Pattern CURRENT_EPISODE_REGEX = Pattern.compile("第(?<e>\\d+)集");
    private static final String NOT_FOUND = "404";
    private static final String INTERNAL_SERVER_ERROR = "500";

    public ImdbCnSite() {
        super("IMDb CN", new BasicHttpSession("imdb.cn"), new ImdbCnResponseHandler());
    }

    @Nonnull
    @Override
    public ImdbTitle findById(String imdbId) throws NotFoundException, OtherResponseException {
        Objects.requireNonNull(imdbId);
        Document document = getDocument(builder0("/title/%s", imdbId), SnapshotStrategies.never());
        Map<String, String> dataset = document.selectFirst("a.e_modify_btn").dataset();
        RequestBuilder builder = builder0("/index/video.editform/index.html")
            .addParameter("m_id", dataset.get("movie_id"))
            .addParameter("location", dataset.get("location"));
        Document editForm = getDocument(builder, SnapshotStrategies.never());
        Map<String, Element> fields = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        Elements items = editForm.select(".item");
        for (Element item : items) {
            if (item.hasClass("items")) {
                Element input = item.selectFirst(CssSelectors.TAG_INPUT);
                fields.put(input.attr(CssSelectors.ATTR_NAME), input);
                continue;
            }
            Elements children = item.selectFirst(".choices").children();
            fields.put(children.get(2).attr(CssSelectors.ATTR_NAME), children.get(1));
        }
        int year = Integer.parseInt(fields.get("year").val());
        Duration duration = null;
        String runtime = fields.get("runtime").val();
        if (StringUtils.isNotBlank(runtime)) {
            duration = Duration.ofMinutes(Integer.parseInt(runtime));
        }

        Elements lis = document.selectFirst("div.item_r").select(CssSelectors.TAG_LI);
        AssertUtils.requireRange(lis.size(), 4, 6);
        ImdbTitle imdbTitle;
        final int size = 4;
        if (lis.size() == size) {
            ImdbMovie movie = new ImdbMovie();
            movie.setYear(year);
            movie.setDuration(duration);
            imdbTitle = movie;
        } else {
            String href = lis.first().selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
            String seriesId = RegexUtils.matchesOrElseThrow(EPISODE_LIST_REGEX, href).group("id");
            if (seriesId.equals(imdbId)) {
                ImdbSeries series = new ImdbSeries();
                series.setRangeYear(new RangeYear(year));
                series.setEpisodes(getEpisodes(imdbId));
                imdbTitle = series;
            } else {
                ImdbEpisode episode = new ImdbEpisode();
                episode.setYear(year);
                episode.setDuration(duration);
                episode.setSeriesId(seriesId);
                imdbTitle = episode;
            }
        }

        imdbTitle.setEnTitle(fields.get("etitle").val());
        if (duration != null) {
            imdbTitle.setRuntimes(Collections.singletonList(duration));
        }
        String showdate = fields.get("showdate00").val();
        if (StringUtils.isNotBlank(showdate)) {
            String[] parts = showdate.split("/");
            List<LocalDate> dates = Arrays.stream(parts).map(String::strip).map(Release::of)
                .map(Release::getDate)
                .collect(Collectors.toList());
            imdbTitle.setReleases(dates);
        }

        return imdbTitle;
    }

    private List<String[]> getEpisodes(String seriesId)
        throws NotFoundException, OtherResponseException {
        Document document = getDocument(
            builder0("/title/%s/episodelist", seriesId).addParameter("season", "1"),
            SnapshotStrategies.never());
        int seasonsCount =
            document.selectFirst("select#ep_season").select(CssSelectors.TAG_OPTION).size() - 1;

        List<String[]> result = new ArrayList<>();
        int currentSeason = 1;
        while (true) {
            Map<Integer, String> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
            int page = 1;
            while (true) {
                Elements items = document.select("div.s_item");
                for (Element item : items) {
                    String href = item.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
                    String id = RegexUtils.matchesOrElseThrow(TITLE_HREF_REGEX, href).group("id");
                    String text = item.selectFirst("span.s2").text();
                    int episode =
                        Integer.parseInt(
                            RegexUtils.matchesOrElseThrow(CURRENT_EPISODE_REGEX, text).group("e"));
                    map.put(episode, id);
                }
                Element pagination = document.selectFirst("ul.pagination");
                if (pagination == null) {
                    break;
                }
                Element nextPage = pagination.select(CssSelectors.TAG_LI).last();
                if (nextPage.hasClass("disabled")) {
                    break;
                }
                page++;
                document = getDocument(builder0("/title/%s/episodelist", seriesId)
                        .addParameter("season", currentSeason)
                        .addParameter("page", page),
                    SnapshotStrategies.never());
            }
            if (map.isEmpty()) {
                result.add(new String[1]);
            } else {
                String[] episodes = new String[Collections.max(map.keySet()) + 1];
                map.forEach((key, value) -> episodes[key] = value);
                result.add(episodes);
            }

            currentSeason++;
            if (currentSeason > seasonsCount) {
                break;
            }
            document = getDocument(
                builder0("/title/%s/episodelist", seriesId)
                    .addParameter("season", currentSeason),
                SnapshotStrategies.never());
        }
        return result;
    }

    private static final class ImdbCnResponseHandler extends StringResponseHandler {

        @Override
        protected String handleContent(String content) throws HttpResponseException {
            if (NOT_FOUND.equals(content)) {
                throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, NOT_FOUND);
            }
            if (INTERNAL_SERVER_ERROR.equals(Jsoup.parse(content).title())) {
                throw new HttpResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR,
                    "Server is limit to access.");
            }
            return content;
        }
    }
}
