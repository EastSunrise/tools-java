package wsg.tools.internet.resource.site.xlc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.CacheStrategy;
import wsg.tools.internet.base.CssSelector;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.impl.ResourceFactory;
import wsg.tools.internet.resource.item.VideoType;
import wsg.tools.internet.resource.site.AbstractRangeResourceSite;
import wsg.tools.internet.video.VideoConstants;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://www.xunleicang.in/">XunLeiCang</a>
 * @since 2020/9/9
 */
@Slf4j
public class XlcSite extends AbstractRangeResourceSite<XlcItem> {

    private static final Pattern ITEM_TITLE_REGEX = Pattern.compile("(?<title>.*)_迅雷下载_高清电影_迅雷仓");
    private static final Pattern YEAR_REGEX = Pattern.compile("\\((?<year>\\d+)\\)");
    private static final Pattern ITEM_HREF_REGEX = Pattern.compile("/vod-read-id-(?<id>\\d+)\\.html");
    private static final Pattern TYPE_PATH_REGEX = Pattern.compile("/vod-show-id-(?<index>\\d+)\\.html");
    private static final VideoType[] TYPES = {
            null, VideoType.MOVIE, VideoType.TV, VideoType.ANIME, VideoType.VARIETY, VideoType.FOUR_K,
            VideoType.FHD, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE,
            VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.TV,
            VideoType.TV, VideoType.TV, VideoType.TV, VideoType.TV, VideoType.THREE_D,
            VideoType.MANDARIN
    };

    private static XlcSite instance;

    private XlcSite() {
        super("XLC", "xunleicang.in");
    }

    public static synchronized XlcSite getInstance() {
        if (instance == null) {
            instance = new XlcSite();
        }
        return instance;
    }

    /**
     * @see <a href="https://www.xunleicang.in/ajax-show-id-new.html">Last Update</a>
     */
    @Override
    protected int max() {
        Document document;
        try {
            document = getDocument(builder0("/ajax-show-id-new.html"), CacheStrategy.ALWAYS_UPDATE);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Elements as = document.selectFirst("ul.f6").select(CssSelector.TAG_A);
        int max = 1;
        for (Element a : as) {
            String id = RegexUtils.matchesOrElseThrow(ITEM_HREF_REGEX, a.attr(CssSelector.ATTR_HREF)).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    @Override
    protected XlcItem getItem(int id) throws NotFoundException {
        URIBuilder builder = builder0("/vod-read-id-%d.html", id);
        Document document = getDocument(builder, CacheStrategy.NEVER_UPDATE);

        Map<String, Node> infos = new HashMap<>(8);
        Elements elements = document.selectFirst(".moviecont").select(CssSelector.TAG_STRONG);
        for (Element strong : elements) {
            infos.put(strong.text(), strong.nextSibling());
        }
        Node node = infos.get("更新时间：");
        LocalDate updateDate = LocalDate.parse(((TextNode) node).text(), DateTimeFormatter.ISO_LOCAL_DATE);
        Elements as = document.selectFirst("div.pleft").selectFirst(CssSelector.TAG_H3).select(CssSelector.TAG_A);
        Matcher matcher = RegexUtils.matchesOrElseThrow(TYPE_PATH_REGEX, as.get(as.size() - 2).attr(CssSelector.ATTR_HREF));
        VideoType type = TYPES[Integer.parseInt(matcher.group("index"))];
        node = infos.get("状态：");
        String state = ((TextNode) node).text();
        XlcItem item = new XlcItem(id, builder.toString(), updateDate, type, state);

        item.setTitle(RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, document.title()).group("title"));
        Element font = as.last().selectFirst(CssSelector.TAG_FONT);
        if (font != null) {
            int year = Integer.parseInt(RegexUtils.matchesOrElseThrow(YEAR_REGEX, font.text()).group("year"));
            if (year >= VideoConstants.FILM_START_YEAR && year <= Year.now().getValue()) {
                item.setYear(year);
            }
        }

        List<AbstractResource> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        Elements lis = document.select("ul.down-list").select("li.item");
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelector.TAG_A);
            String href = a.attr(CssSelector.ATTR_HREF);
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href)) {
                continue;
            }
            String title = a.text().strip();
            try {
                resources.add(ResourceFactory.create(title, href, () -> ResourceFactory.getPassword(title, href)));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);
        return item;
    }
}
