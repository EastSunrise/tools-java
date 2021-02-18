package wsg.tools.internet.video.site.imdb;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.CssSelector;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.site.imdb.pojo.info.ReleaseInfo;
import wsg.tools.internet.video.site.imdb.pojo.info.YearInfo;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <a href="https://imdb.cn">IMDb CN</a>
 *
 * @author Kingen
 * @since 2020/12/12
 */
public class ImdbCnSite extends BaseSite implements ImdbRepository<ImdbTitle> {

    private static final Pattern EPISODE_LIST_REGEX = Pattern.compile("/title/(?<id>tt\\d+)/episodelist\\?season=\\d+");
    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("/title/(?<id>tt\\d+)/");
    private static final Pattern CURRENT_EPISODE_REGEX = Pattern.compile("第(?<e>\\d+)集");
    private static final String NOT_FOUND = "404";
    private static final String INTERNAL_SERVER_ERROR = "500";

    private static ImdbCnSite instance;

    private ImdbCnSite() {
        super("IMDb CN", "imdb.cn");
    }

    public static ImdbCnSite getInstance() {
        if (instance == null) {
            instance = new ImdbCnSite();
        }
        return instance;
    }

    @Override
    public ImdbTitle getItemById(@Nonnull String imdbId) throws NotFoundException {
        Document document = getDocument(builder0("/title/%s", imdbId), SnapshotStrategy.NEVER_UPDATE);

        Map<String, String> dataset = document.selectFirst("a.e_modify_btn").dataset();
        Document editForm = getDocument(builder0("/index/video.editform/index.html")
                .setParameter("m_id", dataset.get("movie_id"))
                .setParameter("location", dataset.get("location")), SnapshotStrategy.NEVER_UPDATE);
        Map<String, Element> fields = new HashMap<>(16);
        Elements items = editForm.select(".item");
        for (Element item : items) {
            if (item.hasClass("items")) {
                Element input = item.selectFirst(CssSelector.TAG_INPUT);
                fields.put(input.attr(CssSelector.ATTR_NAME), input);
                continue;
            }
            Elements children = item.selectFirst(".choices").children();
            fields.put(children.get(2).attr(CssSelector.ATTR_NAME), children.get(1));
        }
        int year = Integer.parseInt(fields.get("year").val());
        Duration duration = null;
        String runtime = fields.get("runtime").val();
        if (StringUtils.isNotBlank(runtime)) {
            duration = Duration.ofMinutes(Integer.parseInt(runtime));
        }

        Elements lis = document.selectFirst("div.item_r").select(CssSelector.TAG_LI);
        AssertUtils.requireRange(lis.size(), 4, 6);
        ImdbTitle imdbTitle;
        final int size = 4;
        if (lis.size() == size) {
            ImdbMovie movie = new ImdbMovie();
            movie.setYear(year);
            movie.setDuration(duration);
            imdbTitle = movie;
        } else {
            String href = lis.first().selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF);
            String seriesId = RegexUtils.matchesOrElseThrow(EPISODE_LIST_REGEX, href).group("id");
            if (seriesId.equals(imdbId)) {
                ImdbSeries series = new ImdbSeries();
                series.setYearInfo(new YearInfo(year));
                try {
                    series.setEpisodes(getEpisodes(imdbId));
                } catch (NotFoundException e) {
                    throw AssertUtils.runtimeException(e);
                }
                imdbTitle = series;
            } else {
                ImdbEpisode episode = new ImdbEpisode();
                episode.setYear(year);
                episode.setDuration(duration);
                episode.setSeriesId(seriesId);
                imdbTitle = episode;
            }
        }

        imdbTitle.setText(fields.get("etitle").val());
        if (duration != null) {
            imdbTitle.setRuntimes(Collections.singletonList(duration));
        }
        String showdate = fields.get("showdate00").val();
        if (StringUtils.isNotBlank(showdate)) {
            String[] parts = showdate.split("/");
            List<LocalDate> dates = Arrays.stream(parts).map(String::strip).map(ReleaseInfo::new).map(ReleaseInfo::getDate).collect(Collectors.toList());
            imdbTitle.setReleases(dates);
        }

        return imdbTitle;
    }

    private List<String[]> getEpisodes(String seriesId) throws NotFoundException {
        Document document = getDocument(builder0("/title/%s/episodelist", seriesId)
                .addParameter("season", "1"), SnapshotStrategy.NEVER_UPDATE);
        int seasonsCount = document.selectFirst("select#ep_season").select(CssSelector.TAG_OPTION).size() - 1;

        List<String[]> result = new ArrayList<>();
        int currentSeason = 1;
        while (true) {
            Map<Integer, String> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
            int page = 1;
            while (true) {
                Elements items = document.select("div.s_item");
                for (Element item : items) {
                    String href = item.selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF);
                    String id = RegexUtils.matchesOrElseThrow(TITLE_HREF_REGEX, href).group("id");
                    String text = item.selectFirst("span.s2").text();
                    int episode = Integer.parseInt(RegexUtils.matchesOrElseThrow(CURRENT_EPISODE_REGEX, text).group("e"));
                    map.put(episode, id);
                }
                Element pagination = document.selectFirst("ul.pagination");
                if (pagination == null) {
                    break;
                }
                Element nextPage = pagination.select(CssSelector.TAG_LI).last();
                if (nextPage.hasClass("disabled")) {
                    break;
                }
                document = getDocument(builder0("/title/%s/episodelist", seriesId)
                        .addParameter("season", String.valueOf(currentSeason))
                        .addParameter("page", String.valueOf(++page)), SnapshotStrategy.NEVER_UPDATE);
            }
            if (map.isEmpty()) {
                result.add(new String[1]);
            } else {
                String[] episodes = new String[Collections.max(map.keySet()) + 1];
                map.forEach((key, value) -> episodes[key] = value);
                result.add(episodes);
            }

            if (++currentSeason > seasonsCount) {
                break;
            }
            document = getDocument(builder0("/title/%s/episodelist", seriesId)
                    .addParameter("season", String.valueOf(currentSeason)), SnapshotStrategy.NEVER_UPDATE);
        }
        return result;
    }

    @Override
    protected String handleEntity(HttpEntity entity) throws IOException {
        String content = super.handleEntity(entity);
        if (NOT_FOUND.equals(content)) {
            throw new NotFoundException(NOT_FOUND);
        }
        if (INTERNAL_SERVER_ERROR.equals(Jsoup.parse(content).title())) {
            throw new HttpResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Server is limit to access.");
        }
        return content;
    }
}
