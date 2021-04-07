package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.jetbrains.annotations.Contract;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.TimeUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.repository.RepoPageable;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.RequestWrapper;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.SiteUtils;
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
public class BabesTubeSite extends BaseSite implements RepoPageable<BabesPageReq, BabesPageResult>,
    RepoRetrievable<String, BabesVideo> {

    private static final String NULL = "N/A";
    private static final String NOT_KNOWN = "Not known";
    private static final String HOME = "https://www.babestube.com";
    private static final double CENTIMETERS_PER_FOOT = 30.48;
    private static final double CENTIMETERS_PER_INCH = 2.54;
    private static final double KILOGRAMS_PER_POUND = 0.4535924;
    private static final Pattern SEPARATOR = Pattern.compile(", ");

    public BabesTubeSite() {
        super("Babes Tube", httpsHost("babestube.com"));
    }

    /**
     * Retrieves paged indices of videos. May contain duplicate indices.
     */
    @Nonnull
    @Override
    public BabesPageResult findPage(@Nonnull BabesPageReq req)
        throws NotFoundException, OtherResponseException {
        return getVideoPage(req, "/" + req.getSortBy().getAsPath() + "/");
    }

    /**
     * Retrieves a video by its path.
     *
     * @see BabesVideoIndex#getAsPath()
     */
    @Nonnull
    @Override
    public BabesVideo findById(@Nonnull String videoPath)
        throws OtherResponseException, NotFoundException {
        Document document = getDocument(httpGet("/videos/%s/", videoPath), t -> false);
        Map<String, String> metadata = DocumentUtils.getMetadata(document);
        String url = metadata.get("og:url");
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.VIDEO_HREF_REGEX, url);
        int id = Integer.parseInt(matcher.group("id"));
        String titlePath = matcher.group("p");
        String title = metadata.get("og:title");
        String description = metadata.get("description");
        URL cover = NetUtils.createURL(metadata.get("og:image"));
        Duration duration = Duration.ofSeconds(Integer.parseInt(metadata.get("video:duration")));
        LocalDateTime uploadTime = LocalDateTime.parse(metadata.get("ya:ovs:upload_date"));
        int views = Integer.parseInt(metadata.get("ya:ovs:views_total"));
        int comments = Integer.parseInt(metadata.get("ya:ovs:comments"));
        double rating = Double.parseDouble(metadata.get("ya:ovs:rating"));

        Element viewlist = document.selectFirst(".viewlist");
        int likes = Integer.parseInt(viewlist.selectFirst(".rate-like").text());
        int dislikes = Integer.parseInt(viewlist.selectFirst(".rate-dislike").text());
        Element member = viewlist.selectFirst(".info").selectFirst(CssSelectors.TAG_A);
        String memberHref = member.attr(CssSelectors.ATTR_HREF);
        Matcher memberMatcher = RegexUtils.matchesOrElseThrow(Lazy.MEMBER_HREF_REGEX, memberHref);
        int authorId = Integer.parseInt(memberMatcher.group("id"));
        BabesMember author = new BabesMember(authorId, member.text());
        BabesVideo video = new BabesVideo(id, titlePath, title, cover, duration, rating, views,
            likes, dislikes, comments, description, author, uploadTime);
        Elements scripts = viewlist.selectFirst(".player-holder").select(CssSelectors.TAG_SCRIPT);
        if (!scripts.isEmpty()) {
            String script = scripts.last().html();
            Matcher srcMatcher = RegexUtils.findOrElseThrow(Lazy.VIDEO_URL_REGEX, script);
            video.setSource(NetUtils.createURL(srcMatcher.group("u")));
        }
        String tags = metadata.get("video:tag");
        if (tags != null) {
            video.setTags(SEPARATOR.split(tags));
        }
        String quality = metadata.get("ya:ovs:quality");
        if (quality != null) {
            video.setQuality(VideoQuality.valueOf(quality));
        }
        return video;
    }

    /**
     * Retrieves all categories.
     */
    public List<BabesCategory> findAllCategories()
        throws NotFoundException, OtherResponseException {
        Block block = getBlock("list_categories_categories_list", "/categories/", 0, "title");
        List<BabesCategory> categories = new ArrayList<>(block.items.size());
        for (Element item : block.items) {
            Element th = item.selectFirst(".th");
            String href = th.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.CATEGORY_HREF_REGEX, href);
            String titlePath = matcher.group("p");
            String title = th.attr(CssSelectors.ATTR_TITLE);
            String cover = th.selectFirst(".lazyload").dataset().get(CssSelectors.ATTR_SRC);
            String countText = th.selectFirst(".count").text();
            int count = Integer.parseInt(countText.substring(0, countText.length() - 7));
            categories.add(new BabesCategory(titlePath, title, NetUtils.createURL(cover), count));
        }
        return categories;
    }

    /**
     * Retrieves paged indices of videos under the given category.
     *
     * @see BabesCategory#getAsPath()
     */
    public BabesPageResult findPageByCategory(String categoryPath, BabesPageReq req)
        throws NotFoundException, OtherResponseException {
        return getVideoPage(req, String.format("/categories/%s/", categoryPath));
    }

    /**
     * Retrieves paged indices of models.
     */
    @Nonnull
    public BabesModelPageResult findModelPage(@Nonnull BabesModelPageReq req)
        throws NotFoundException, OtherResponseException {
        Block block = getBlock("list_models_models_list", "/models/", req.getCurrent(),
            req.getSortBy().getAsPath()
        );
        List<BabesModelIndex> indices = new ArrayList<>(block.items.size());
        for (Element child : block.items) {
            String modelHref = child.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
            String p = RegexUtils.matchesOrElseThrow(Lazy.MODEL_HREF_REGEX, modelHref).group("p");
            String name = child.selectFirst(".author").text();
            String cover = child.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);
            Elements cols = child.selectFirst(".count").children();
            String videosText = cols.first().text();
            int videos = Integer.parseInt(videosText.substring(0, videosText.length() - 7));
            String photosText = cols.last().text();
            int photos = Integer.parseInt(photosText.substring(0, photosText.length() - 7));
            indices.add(new BabesModelIndex(p, name, NetUtils.createURL(cover), videos, photos));
        }
        return new BabesModelPageResult(indices, req, block.totalPages);
    }

    /**
     * Retrieves the details of a model by the given name as path.
     *
     * @see BabesModelIndex#getAsPath()
     */
    public BabesModel findModel(@Nonnull String namePath)
        throws NotFoundException, OtherResponseException {
        Document document = getDocument(httpGet("/models/%s/", namePath), t -> true);
        Element main = document.selectFirst(".main");
        String src = main.selectFirst(".model").selectFirst(".thumb").attr(CssSelectors.ATTR_SRC);
        URL cover = NetUtils.createURL(src);
        Element head = main.selectFirst(".head");
        String name = head.selectFirst(CssSelectors.TAG_H1).text();
        Elements cols = head.selectFirst(".information_row").select(".col");
        Map<String, String> counts = cols.stream().map(col -> col.children().first())
            .collect(Collectors.toMap(Element::text, e -> ((TextNode) e.nextSibling()).text()));
        int videos = Integer.parseInt(counts.get("videos:").strip());
        int photos = Integer.parseInt(counts.get("photos:").strip());
        BabesModel model = new BabesModel(namePath, name, cover, videos, photos);
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
        BabesPageReq first = BabesPageReq.first();
        String path = String.format("/models/%s/", namePath);
        model.setVideoIndices(SiteUtils.collectPage(req -> getVideoPage(req, path), first));
        return model;
    }

    @Nonnull
    @Contract("_, _ -> new")
    private BabesPageResult getVideoPage(@Nonnull BabesPageReq req, String path)
        throws NotFoundException, OtherResponseException {
        String blockId = "list_videos_common_videos_list";
        Block block = getBlock(blockId, path, req.getCurrent(), req.getSortBy().getArgument());
        List<BabesVideoIndex> indices = new ArrayList<>(block.items.size());
        for (Element item : block.items) {
            Element a = item.selectFirst(".th");
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.VIDEO_HREF_REGEX, href);
            int id = Integer.parseInt(matcher.group("id"));
            String titlePath = matcher.group("p");
            String title = a.attr(CssSelectors.ATTR_TITLE);
            Map<String, String> dataset = item.selectFirst(".lazyload").dataset();
            URL cover = NetUtils.createURL(dataset.get("src"));
            Duration duration = TimeUtils.parseDuration(item.selectFirst(".time").text());
            String rate = item.selectFirst(".rate").text();
            double rating = Integer.parseInt(rate.substring(0, rate.length() - 1)) / 100.0D;
            Elements info = item.select(".count");
            String viewsText = info.first().text().replace(" ", "");
            int views = Integer.parseInt(viewsText.substring(0, viewsText.length() - 5));
            BabesVideoIndex index = new BabesVideoIndex(id, titlePath, title, cover, duration,
                rating, views);
            String preview = dataset.get("preview");
            if (preview != null) {
                index.setPreview(NetUtils.createURL(preview));
            }
            Element quality = item.selectFirst(".quality");
            if (quality != null) {
                index.setQuality(VideoQuality.valueOf(quality.text()));
            }
            indices.add(index);
        }
        return new BabesPageResult(indices, req, block.totalPages);
    }

    @Nonnull
    @Contract("_, _, _, _ -> new")
    private Block getBlock(String blockId, String path, int page, Object sortBy)
        throws NotFoundException, OtherResponseException {
        if (page > 0) {
            path += (page + 1) + "/";
        }
        RequestWrapper wrapper = httpGet(path)
            .addParameter("mode", "async")
            .addParameter("function", "get_block")
            .addParameter("block_id", blockId)
            .addParameter("sort_by", sortBy);
        Document document = getDocument(wrapper, t -> true);
        Elements items = document.selectFirst("#" + blockId + "_items").children();
        int totalPages = 1;
        Element pagination = document.selectFirst(".pagination");
        if (pagination != null) {
            Element last = pagination.children().last();
            String active = "active";
            if (!last.hasClass(active)) {
                last = last.previousElementSibling();
            }
            totalPages = Integer.parseInt(last.text());
        }
        return new Block(items, totalPages);
    }

    private static final class Block {

        private final Elements items;
        private final int totalPages;

        private Block(Elements items, int totalPages) {
            this.items = items;
            this.totalPages = totalPages;
        }
    }

    private static class Lazy {

        private static final Pattern MODEL_HREF_REGEX = Pattern
            .compile(HOME + "/models/(?<p>[a-z\\d-]+)/");
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
        private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM yyyy").localizedBy(Locale.US);
        private static final Pattern MEMBER_HREF_REGEX = Pattern
            .compile(HOME + "/members/(?<id>\\d+)/");
        private static final Pattern CATEGORY_HREF_REGEX = Pattern
            .compile(HOME + "/categories/(?<p>[A-Za-z-]+)/");
        private static final Pattern VIDEO_URL_REGEX = Pattern
            .compile("video_url: '(?<u>[\\w:/.]+)'");
    }
}
