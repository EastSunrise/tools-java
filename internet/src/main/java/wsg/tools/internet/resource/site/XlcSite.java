package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
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
import wsg.tools.internet.resource.entity.resource.base.Resource;
import wsg.tools.internet.resource.entity.resource.base.UnknownResource;

import javax.annotation.Nonnull;
import java.net.URI;
import java.time.Year;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://aixiaoju.com">AXJ</a>
 * @since 2020/9/9
 */
@Slf4j
public class XlcSite extends BaseResourceSite<SimpleItem> {

    private static final Pattern ITEM_TITLE_REGEX = Pattern.compile("(?<title>.*)_迅雷下载_高清电影_迅雷仓");
    private static final Pattern YEAR_REGEX = Pattern.compile("\\((?<year>\\d+)\\)");
    private static final Pattern TYPE_PATH_REGEX = Pattern.compile("/vod-show-id-(?<index>\\d+).html");

    private static final Pattern ITEM_HREF_REGEX =
            Pattern.compile("(https://www\\.(xunleicang\\.in|xlc2020\\.com))?(?<path>/vod-read-id-\\d+.html)");
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
    protected List<URI> getAllUris() {
        return getUrisById(1, 43238, id -> createUri0("/vod-read-id-%d.html", id));
    }

    @Override
    protected final Set<URI> searchItems(@Nonnull String keyword) {
        List<BasicNameValuePair> params = Collections.singletonList(new BasicNameValuePair("wd", keyword));
        Document document;
        try {
            document = postDocument(builder0("/vod-search"), params, true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Set<URI> uris = new HashSet<>();
        String movList = "div.movList4";
        for (Element div : document.select(movList)) {
            Element h3 = div.selectFirst(TAG_H3);
            Element a = h3.selectFirst(TAG_A);
            Matcher matcher = ITEM_HREF_REGEX.matcher(a.attr(ATTR_HREF));
            if (!matcher.matches()) {
                continue;
            }
            uris.add(createUri0(matcher.group("path")));
        }
        return uris;
    }

    @Override
    protected final SimpleItem getItem(@Nonnull URI uri) throws NotFoundException {
        Document document = getDocument(new URIBuilder(uri), true);
        SimpleItem item = new SimpleItem(uri.toString());
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

        List<Resource> resources = new LinkedList<>();
        Elements lis = document.select("ul.down-list").select("li.item");
        for (Element li : lis) {
            Element a = li.selectFirst(TAG_A);
            String href = a.attr(ATTR_HREF);
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href)) {
                continue;
            }
            resources.add(ResourceFactory.create(a.text().strip(), href, () -> new UnknownResource(href)));
        }
        item.setResources(resources);

        return item;
    }
}
