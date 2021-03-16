package wsg.tools.internet.info.adult;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.Repositories;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.IndicesRepository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.UnexpectedException;
import wsg.tools.internet.enums.Color;
import wsg.tools.internet.info.adult.common.CupEnum;
import wsg.tools.internet.info.adult.common.Measurements;
import wsg.tools.internet.info.adult.common.VideoQuality;

/**
 * @author Kingen
 * @see <a href="https://www.babestube.com/">babestube</a>
 * @since 2021/3/15
 */
public class BabesTubeSite extends BaseSite {

    private static final String NULL = "N/A";
    private static final String NOT_KNOWN = "Not known";
    private static final String HOME = "https://www.babestube.com";
    private static final Pattern MODEL_HREF_REGEX = Pattern
        .compile(HOME + "/models/(?<id>[a-z\\d-]+)/");
    private static final Pattern COUNT_REGEX = Pattern.compile("(?<c>\\d+) (videos|photos)");
    private static final Pattern HEIGHT_REGEX = Pattern.compile("(?<h>\\d{3}) cm");
    private static final Pattern EN_HEIGHT_REGEX = Pattern.compile("(?<f>\\d)'(?<i>\\d{1,2})\"");
    private static final double CENTIMETERS_PER_FOOT = 30.48;
    private static final double CENTIMETERS_PER_INCH = 2.54;
    private static final Pattern WEIGHT_REGEX = Pattern.compile("(?<w>\\d{2}) kg");
    private static final Pattern EN_WEIGHT_REGEX = Pattern.compile("(?<p>\\d{2,3}) lbs");
    private static final double KILOMETERS_PER_POUND = 0.4535924;
    private static final Pattern MEASURE_REGEX = Pattern
        .compile("(?<b>\\d{2})(-?(?<c>([A-N])\\4?|DDD))?"
            + "(([-/])((?<w>\\d{2})|\\?\\?)\\6?((?<h>\\d{1,2})|\\?\\?)( i)?)?");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy")
        .localizedBy(Locale.US);
    private static final Pattern VIDEO_HREF_REGEX = Pattern
        .compile(HOME + "/videos/(?<id>\\d+)/(?<p>[a-z\\d-]+)/");
    private static final Pattern DURATION_REGEX = Pattern
        .compile("((?<h>\\d):)?(?<m>\\d{1,2}):(?<s>\\d{2})");
    private static final Pattern VIEWS_REGEX = Pattern.compile("(?<v>\\d+)views");

    public BabesTubeSite() {
        super("Babes Tube", new BasicHttpSession("babestube.com"));
    }

    /**
     * Returns the repository of models based on the indices.
     */
    public IndicesRepository<BabesModel> getModelRepository(boolean withVideos)
        throws HttpResponseException {
        BabesPageRequest.OrderBy orderBy = BabesPageRequest.OrderBy.LAST_UPDATE;
        BabesPageRequest request = new BabesPageRequest(0, orderBy);
        List<BabesModelIndex> indices = new ArrayList<>();
        while (true) {
            BabesModelPageResult result = findAllModelIndices(request);
            indices.addAll(result.getContent());
            if (!result.hasNext()) {
                break;
            }
            request = request.next();
        }
        return Repositories.indices(index -> findModel(index, withVideos), indices);
    }

    /**
     * Finds paged indices of models.
     */
    public BabesModelPageResult findAllModelIndices(@Nonnull BabesPageRequest request)
        throws HttpResponseException {
        String page = request.getCurrent() == 0 ? "" : (request.getCurrent() + 1) + "/";
        RequestBuilder builder = builder0("/models/%s", page)
            .addParameter("mode", "async")
            .addParameter("function", "get_block")
            .addParameter("block_id", "list_models_models_list")
            .addParameter("sort_by", request.getOrderBy().getText());
        Document document = getDocument(builder, SnapshotStrategy.always());
        Elements children = document.selectFirst("#list_models_models_list_items").children();
        List<BabesModelIndex> indices = new ArrayList<>();
        for (Element child : children) {
            String modelHref = child.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
            String id = RegexUtils.matchesOrElseThrow(MODEL_HREF_REGEX, modelHref).group("id");
            String name = child.selectFirst(".author").text();
            String cover = child.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);
            Elements cols = child.selectFirst(".count").children();
            Matcher vMatcher = RegexUtils.matchesOrElseThrow(COUNT_REGEX, cols.first().text());
            int videos = Integer.parseInt(vMatcher.group("c"));
            Matcher iMatcher = RegexUtils.matchesOrElseThrow(COUNT_REGEX, cols.last().text());
            int photos = Integer.parseInt(iMatcher.group("c"));
            indices.add(new BabesModelIndex(id, name, cover, videos, photos));
        }
        Element last = document.selectFirst(".pagination").children().last();
        if (!last.hasClass("active")) {
            last = last.previousElementSibling();
        }
        int totalPages = Integer.parseInt(last.text());
        return new BabesModelPageResult(indices, request, totalPages);
    }

    public BabesModel findModel(@Nonnull BabesModelIndex index, boolean withVideos)
        throws HttpResponseException {
        String id = index.getId();
        RequestBuilder builder = builder0("/models/%s/", id);
        Document document = getDocument(builder,
            doc -> getCounts(doc.selectFirst(".head")).getLeft() < index.getVideos());
        Element main = document.selectFirst(".main");
        String cover = main.selectFirst(".model").selectFirst(".thumb").attr(CssSelectors.ATTR_SRC);
        Element head = main.selectFirst(".head");
        String name = head.selectFirst(CssSelectors.TAG_H1).text();
        Pair<Integer, Integer> counts = getCounts(head);
        BabesModel model = new BabesModel(id, name, cover, counts.getLeft(), counts.getRight());
        Elements options = head.selectFirst(".list").select(CssSelectors.TAG_LI);
        Map<String, String> info = options.stream().map(Element::children)
            .collect(Collectors.toMap(es -> es.first().text(), es -> es.last().text()));
        model.setHeight(getValue(info, "Height", s -> {
            Matcher matcher = HEIGHT_REGEX.matcher(s);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group("h"));
            }
            Matcher enMatcher = RegexUtils.findOrElseThrow(EN_HEIGHT_REGEX, s);
            int feet = Integer.parseInt(enMatcher.group("f"));
            int inches = Integer.parseInt(enMatcher.group("i"));
            return (int) Math.round(feet * CENTIMETERS_PER_FOOT + inches * CENTIMETERS_PER_INCH);
        }));
        model.setWeight(getValue(info, "Weight", s -> {
            Matcher matcher = WEIGHT_REGEX.matcher(s);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group("w"));
            }
            Matcher enMatcher = RegexUtils.findOrElseThrow(EN_WEIGHT_REGEX, s);
            int pounds = Integer.parseInt(enMatcher.group("p"));
            return (int) Math.round(pounds * KILOMETERS_PER_POUND);
        }));
        model.setMeasurements(getValue(info, "Measurements", s -> {
            Matcher matcher = RegexUtils.matchesOrElseThrow(MEASURE_REGEX, s);
            int bust = Integer.parseInt(matcher.group("b"));
            Measurements measure = new Measurements(bust);
            String waist = matcher.group("w");
            if (waist != null) {
                measure.setWaist(Integer.parseInt(waist));
            }
            String hip = matcher.group("h");
            if (hip != null) {
                measure.setHip(Integer.parseInt(hip));
            }
            String cup = matcher.group("c");
            if (cup != null) {
                measure.setCup(Enum.valueOf(CupEnum.class, cup.substring(0, 1)));
            }
            return measure;
        }));
        model.setHairColor(getColor(info, "Hair color"));
        model.setEyeColor(getColor(info, "Eye color"));
        model.setBirthday(getDate(info, "Birth date"));
        model.setBirthplace(getString(info, "Birth place"));
        model.setWebsite(getString(info, "Official website"));
        if (withVideos) {
            model.setVideoIndices(findVideoIndicesByModel(index));
        }
        return model;
    }

    public List<BabesVideoIndex> findVideoIndicesByModel(@Nonnull BabesModelIndex modelIndex)
        throws HttpResponseException {
        List<BabesVideoIndex> indices = getVideoIndices(modelIndex, SnapshotStrategy.never());
        if (indices.size() < modelIndex.getVideos()) {
            indices = getVideoIndices(modelIndex, SnapshotStrategy.always());
        }
        return indices;
    }

    private List<BabesVideoIndex> getVideoIndices(@Nonnull BabesModelIndex modelIndex,
        @Nonnull SnapshotStrategy<Document> strategy) throws HttpResponseException {
        RequestBuilder builder = builder0("/models/%s/", modelIndex.getId())
            .addParameter("mode", "async")
            .addParameter("function", "get_block")
            .addParameter("block_id", "list_videos_common_videos_list")
            .addParameter("sort_by", "post_date");
        List<BabesVideoIndex> indices = new ArrayList<>();
        while (true) {
            Document document = getDocument(builder, strategy);
            Element list = document.selectFirst("#list_videos_common_videos_list_items");
            for (Element child : list.children()) {
                String href = child.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
                Matcher matcher = RegexUtils.matchesOrElseThrow(VIDEO_HREF_REGEX, href);
                int id = Integer.parseInt(matcher.group("id"));
                String path = matcher.group("p");
                String title = child.selectFirst(".title").text();
                BabesVideoIndex index = new BabesVideoIndex(id, path, title);
                Element img = child.selectFirst(".lazyload");
                index.setCover(img.attr(CssSelectors.ATTR_SRC));
                index.setPreview(img.dataset().get("preview"));
                Element quality = child.selectFirst(".quality");
                if (quality != null) {
                    index.setQuality(Enum.valueOf(VideoQuality.class, quality.text()));
                }
                String durationStr = child.selectFirst(".time").text();
                Matcher durationMatch = RegexUtils.matchesOrElseThrow(DURATION_REGEX, durationStr);
                int minutes = Integer.parseInt(durationMatch.group("m"));
                int seconds = Integer.parseInt(durationMatch.group("s"));
                Duration duration = Duration.ofMinutes(minutes).plusSeconds(seconds);
                String hours = durationMatch.group("h");
                if (hours != null) {
                    duration = duration.plusHours(Integer.parseInt(hours));
                }
                index.setDuration(duration);
                index.setAuthor(child.selectFirst(".name").text());
                String rate = child.selectFirst(".rate").text();
                try {
                    index.setRating(NumberFormat.getPercentInstance().parse(rate).doubleValue());
                } catch (ParseException e) {
                    throw new UnexpectedException(e);
                }
                String views = child.selectFirst(".count").text().replace(" ", "");
                Matcher viewMatcher = RegexUtils.matchesOrElseThrow(VIEWS_REGEX, views);
                index.setViews(Integer.parseInt(viewMatcher.group("v")));
                indices.add(index);
            }
            Element pagination = document.selectFirst(".pagination");
            if (pagination == null) {
                break;
            }
            Element last = pagination.children().last();
            String next = last.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
            if (StringUtils.isBlank(next)) {
                break;
            }
            builder.setPath(next);
        }
        return indices;
    }

    private Pair<Integer, Integer> getCounts(Element head) {
        Elements cols = head.selectFirst(".information_row").select(".col");
        Map<String, String> counts = cols.stream().map(col -> col.children().first())
            .collect(Collectors.toMap(Element::text, e -> ((TextNode) e.nextSibling()).text()));
        int videos = Integer.parseInt(counts.get("videos:").strip());
        int photos = Integer.parseInt(counts.get("photos:").strip());
        return Pair.of(videos, photos);
    }

    private Color getColor(Map<String, String> map, String key) {
        return getValue(map, key, s -> EnumUtilExt.deserializeIgnoreCase(s, Color.class));
    }

    private LocalDate getDate(Map<String, String> map, String key) {
        return getValue(map, key, s -> LocalDate.parse(s, FORMATTER));
    }

    private String getString(Map<String, String> map, String key) {
        return getValue(map, key, Function.identity());
    }

    private <T> T getValue(Map<String, String> map, String key, Function<String, T> function) {
        return MapUtilsExt.getValue(map, s -> {
            if (NULL.equals(s) || NOT_KNOWN.equals(s)) {
                return null;
            }
            return function.apply(s);
        }, key);
    }
}
