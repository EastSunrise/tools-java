package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.VideoConstants;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.entity.item.impl.SimpleItem;
import wsg.tools.internet.resource.entity.resource.ResourceFactory;
import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;

import javax.annotation.Nonnull;
import java.time.Year;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://www.xlc2020.com/">XunLeiCang</a>
 * @since 2020/9/9
 */
@Slf4j
public class XlcSite extends BaseResourceSite<SimpleItem> {

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

    public XlcSite() {
        super("XLC", "www.xunleicang.in", 0.1);
    }

    @Override
    public List<SimpleItem> findAll() {
        List<String> paths = getPathsById(1, getMaxId(), id -> String.format("/vod-read-id-%d.html", id));
        return findAllByPathsIgnoreNotFound(paths, this::getItem);
    }

    /**
     * @see <a href="https://www.xlc2020.com/ajax-show-id-new.html">Last Update</a>
     */
    private int getMaxId() {
        Document document;
        try {
            document = getDocument(builder0("/ajax-show-id-new.html"), false);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Elements as = document.selectFirst("ul.f6").select(TAG_A);
        int max = 1;
        for (Element a : as) {
            String id = RegexUtils.matchesOrElseThrow(ITEM_HREF_REGEX, a.attr(ATTR_HREF)).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    private SimpleItem getItem(@Nonnull String path) throws NotFoundException {
        URIBuilder builder = builder0(path);
        Document document = getDocument(builder, true);
        SimpleItem item = new SimpleItem(builder.toString());
        item.setTitle(RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, document.title()).group("title"));

        Elements as = document.selectFirst("div.pleft").selectFirst(TAG_H3).select(TAG_A);
        Matcher matcher = RegexUtils.matchesOrElseThrow(TYPE_PATH_REGEX, as.get(as.size() - 2).attr(ATTR_HREF));
        item.setType(TYPES[Integer.parseInt(matcher.group("index"))]);
        Element font = as.last().selectFirst(TAG_FONT);
        if (font != null) {
            int year = Integer.parseInt(RegexUtils.matchesOrElseThrow(YEAR_REGEX, font.text()).group("year"));
            if (year >= VideoConstants.FILM_START_YEAR && year <= Year.now().getValue()) {
                item.setYear(year);
            }
        }

        List<ValidResource> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        Elements lis = document.select("ul.down-list").select("li.item");
        for (Element li : lis) {
            Element a = li.selectFirst(TAG_A);
            String href = a.attr(ATTR_HREF);
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href)) {
                continue;
            }
            try {
                resources.add(ResourceFactory.create(a.text().strip(), href, null));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);
        return item;
    }
}
