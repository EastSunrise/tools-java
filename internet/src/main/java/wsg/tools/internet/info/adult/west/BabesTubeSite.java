package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.TimeUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.page.BasicPageReq;
import wsg.tools.internet.base.page.CountablePageResult;
import wsg.tools.internet.base.page.PageReq;
import wsg.tools.internet.base.page.PageResult;
import wsg.tools.internet.base.repository.RepoPageable;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.RequestBuilder;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.SiteUtils;
import wsg.tools.internet.common.UnexpectedException;
import wsg.tools.internet.common.enums.Color;
import wsg.tools.internet.info.adult.common.CupEnum;
import wsg.tools.internet.info.adult.common.Measurements;
import wsg.tools.internet.info.adult.common.VideoQuality;

/**
 * @author Kingen
 * @see <a href="https://www.babestube.com/">babestube</a>
 * @since 2021/3/15
 */
@ConcreteSite
public class BabesTubeSite extends BaseSite
    implements RepoPageable<BabesPageReq, BabesModelPageResult> {

    private static final String NULL = "N/A";
    private static final String NOT_KNOWN = "Not known";
    private static final String HOME = "https://www.babestube.com";
    private static final double CENTIMETERS_PER_FOOT = 30.48;
    private static final double CENTIMETERS_PER_INCH = 2.54;
    private static final double KILOGRAMS_PER_POUND = 0.4535924;

    public BabesTubeSite() {
        super("Babes Tube", new BasicHttpSession("babestube.com"));
    }

    /**
     * Retrieves paged indices of models.
     *
     * @see <a href="https://www.babestube.com/models/">Models</a>
     */
    @Override
    @Nonnull
    public BabesModelPageResult findPage(@Nonnull BabesPageReq request)
        throws NotFoundException, OtherResponseException {
        String page = request.getCurrent() == 0 ? "" : (request.getCurrent() + 1) + "/";
        RequestBuilder builder = builder0("/models/%s", page)
            .addParameter("mode", "async")
            .addParameter("function", "get_block")
            .addParameter("block_id", "list_models_models_list")
            .addParameter("sort_by", request.getOrderBy().getText());
        Document document = getDocument(builder, t -> true);
        Elements children = document.selectFirst("#list_models_models_list_items").children();
        List<BabesModelIndex> indices = new ArrayList<>();
        for (Element child : children) {
            String modelHref = child.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
            String id = RegexUtils.matchesOrElseThrow(Lazy.MODEL_HREF_REGEX, modelHref).group("id");
            String name = child.selectFirst(".author").text();
            String cover = child.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);
            Elements cols = child.selectFirst(".count").children();
            Matcher vMatcher = RegexUtils.matchesOrElseThrow(Lazy.COUNT_REGEX, cols.first().text());
            int videos = Integer.parseInt(vMatcher.group("c"));
            Matcher iMatcher = RegexUtils.matchesOrElseThrow(Lazy.COUNT_REGEX, cols.last().text());
            int photos = Integer.parseInt(iMatcher.group("c"));
            indices.add(new BabesModelIndex(id, name, NetUtils.createURL(cover), videos, photos));
        }
        return new BabesModelPageResult(indices, request, getTotalPages(document));
    }

    /**
     * Retrieves the details of a model by the given index with indices of her videos if specified.
     *
     * @param withVideos whether to retrieve the indices of the model's videos at the same time
     */
    public BabesModel findModel(@Nonnull BabesModelIndex index, boolean withVideos)
        throws NotFoundException, OtherResponseException {
        String id = index.getId();
        RequestBuilder builder = builder0("/models/%s/", id);
        Document document = getDocument(builder,
            doc -> getCounts(doc.selectFirst(".head")).getLeft() != index.getVideos());
        Element main = document.selectFirst(".main");
        String src = main.selectFirst(".model").selectFirst(".thumb").attr(CssSelectors.ATTR_SRC);
        URL cover = NetUtils.createURL(src);
        Element head = main.selectFirst(".head");
        String name = head.selectFirst(CssSelectors.TAG_H1).text();
        Pair<Integer, Integer> counts = getCounts(head);
        BabesModel model = new BabesModel(id, name, cover, counts.getLeft(), counts.getRight());
        Elements options = head.selectFirst(".list").select(CssSelectors.TAG_LI);
        Map<String, String> info = new HashMap<>(8);
        for (Element option : options) {
            Elements es = option.children();
            String value = es.last().text();
            if (NULL.equals(value) || NOT_KNOWN.equals(value)) {
                continue;
            }
            MapUtilsExt.putIfAbsent(info, es.first().text(), value);
        }
        model.setHeight(MapUtilsExt.getValue(info, s -> {
            Matcher matcher = Lazy.HEIGHT_REGEX.matcher(s);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group("h"));
            }
            Matcher enMatcher = RegexUtils.findOrElseThrow(Lazy.EN_HEIGHT_REGEX, s);
            int feet = Integer.parseInt(enMatcher.group("f"));
            int inches = Integer.parseInt(enMatcher.group("i"));
            return (int) Math.round(feet * CENTIMETERS_PER_FOOT + inches * CENTIMETERS_PER_INCH);
        }, "Height"));
        model.setWeight(MapUtilsExt.getValue(info, s -> {
            Matcher matcher = Lazy.WEIGHT_REGEX.matcher(s);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group("w"));
            }
            Matcher enMatcher = RegexUtils.findOrElseThrow(Lazy.EN_WEIGHT_REGEX, s);
            int pounds = Integer.parseInt(enMatcher.group("p"));
            return (int) Math.round(pounds * KILOGRAMS_PER_POUND);
        }, "Weight"));
        model.setMeasurements(MapUtilsExt.getValue(info, s -> {
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.MEASURE_REGEX, s);
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
        }, "Measurements"));
        model.setHairColor(MapUtilsExt.getEnumOf(info, Color.class, true, "Hair color"));
        model.setEyeColor(MapUtilsExt.getEnumOf(info, Color.class, true, "Eye color"));
        model.setBirthday(
            MapUtilsExt.getValue(info, s -> LocalDate.parse(s, Lazy.FORMATTER), "Birth date"));
        model.setBirthplace(MapUtilsExt.getString(info, "Birth place"));
        model.setWebsite(MapUtilsExt.getString(info, "Official website"));
        if (withVideos) {
            PageReq firstReq = new BasicPageReq(0, 0);
            List<BabesVideoIndex> indices = SiteUtils
                .collectPage(req -> findVideoPage(index, req), firstReq);
            model.setVideoIndices(indices);
        }
        return model;
    }

    /**
     * Retrieves paged result of indices of videos by the given model and pagination information.
     * <p>
     * Note that {@link PageReq#getPageSize()} is ignored.
     */
    public PageResult<BabesVideoIndex, PageReq> findVideoPage(@Nonnull BabesModelIndex modelIndex,
        @Nonnull PageReq request) throws NotFoundException, OtherResponseException {
        String page = request.getCurrent() == 0 ? "" : (request.getCurrent() + 1) + "/";
        RequestBuilder builder = builder0("/models/%s/%s", modelIndex.getId(), page)
            .addParameter("mode", "async")
            .addParameter("function", "get_block")
            .addParameter("block_id", "list_videos_common_videos_list")
            .addParameter("sort_by", "post_date");
        Document document = getDocument(builder, t -> true);
        List<BabesVideoIndex> indices = new ArrayList<>();
        Element list = document.selectFirst("#list_videos_common_videos_list_items");
        for (Element child : list.children()) {
            String href = child.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.VIDEO_HREF_REGEX, href);
            int id = Integer.parseInt(matcher.group("id"));
            String path = matcher.group("p");
            String title = child.selectFirst(".title").text();
            BabesVideoIndex index = new BabesVideoIndex(id, path, title);
            Element img = child.selectFirst(".lazyload");
            index.setCover(NetUtils.createURL(img.attr(CssSelectors.ATTR_SRC)));
            index.setPreview(NetUtils.createURL(img.dataset().get("preview")));
            Element quality = child.selectFirst(".quality");
            if (quality != null) {
                index.setQuality(Enum.valueOf(VideoQuality.class, quality.text()));
            }
            index.setDuration(TimeUtils.parseDuration(child.selectFirst(".time").text()));
            index.setAuthor(child.selectFirst(".name").text());
            String rate = child.selectFirst(".rate").text();
            try {
                index.setRating(NumberFormat.getPercentInstance().parse(rate).doubleValue());
            } catch (ParseException e) {
                throw new UnexpectedException(e);
            }
            String views = child.selectFirst(".count").text().replace(" ", "");
            Matcher viewMatcher = RegexUtils.matchesOrElseThrow(Lazy.VIEWS_REGEX, views);
            index.setViews(Integer.parseInt(viewMatcher.group("v")));
            indices.add(index);
        }
        return new CountablePageResult<>(indices, request, getTotalPages(document));
    }

    private Pair<Integer, Integer> getCounts(Element head) {
        Elements cols = head.selectFirst(".information_row").select(".col");
        Map<String, String> counts = cols.stream().map(col -> col.children().first())
            .collect(Collectors.toMap(Element::text, e -> ((TextNode) e.nextSibling()).text()));
        int videos = Integer.parseInt(counts.get("videos:").strip());
        int photos = Integer.parseInt(counts.get("photos:").strip());
        return Pair.of(videos, photos);
    }

    private int getTotalPages(Document document) {
        Element pagination = document.selectFirst(".pagination");
        if (pagination == null) {
            return 1;
        }
        Element last = pagination.children().last();
        if (!last.hasClass("active")) {
            last = last.previousElementSibling();
        }
        return Integer.parseInt(last.text());
    }

    private static class Lazy {

        private static final Pattern MODEL_HREF_REGEX = Pattern
            .compile(HOME + "/models/(?<id>[a-z\\d-]+)/");
        private static final Pattern COUNT_REGEX = Pattern.compile("(?<c>\\d+) (videos|photos)");
        private static final Pattern HEIGHT_REGEX = Pattern.compile("(?<h>\\d{3}) cm");
        private static final Pattern EN_HEIGHT_REGEX = Pattern
            .compile("(?<f>\\d)'(?<i>\\d{1,2})\"");
        private static final Pattern WEIGHT_REGEX = Pattern.compile("(?<w>\\d{2}) kg");
        private static final Pattern EN_WEIGHT_REGEX = Pattern.compile("(?<p>\\d{2,3}) lbs");
        private static final Pattern MEASURE_REGEX = Pattern
            .compile("(?<b>\\d{2})(-?(?<c>([A-N])\\4?|DDD))?"
                + "(([-/])((?<w>\\d{2})|\\?\\?)\\6?((?<h>\\d{1,2})|\\?\\?)( i)?)?");
        private static final Pattern VIDEO_HREF_REGEX = Pattern
            .compile(HOME + "/videos/(?<id>\\d+)/(?<p>[a-z\\d-]+)/");
        private static final Pattern VIEWS_REGEX = Pattern.compile("(?<v>\\d+)views");
        private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM yyyy").localizedBy(Locale.US);
    }
}
