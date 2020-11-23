package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
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
import java.util.stream.Collectors;

/**
 * @author Kingen
 * @see <a href="https://993dy.com">Movie Heaven</a>
 * @since 2020/10/18
 */
@Slf4j
public class MovieHeavenSite extends BaseResourceSite<SimpleItem> {

    private static final String TIP_TITLE = "系统提示";
    private static final Pattern ITEM_TITLE_REGEX =
            Pattern.compile("《(?<title>.+)》迅雷下载_(BT种子磁力|全集|最新一期)下载 - LOL电影天堂");
    private static final Pattern TYPE_PATH_REGEX = Pattern.compile("/vod-type-id-(?<index>\\d+)-pg-1.html");
    private static final String UNKNOWN_YEAR = "未知";
    private static final Pattern VAR_URL_REGEX = Pattern.compile("var downurls=\"(?<entries>.*)#\";");
    private static final Pattern RESOURCE_REGEX = Pattern.compile("(?<title>(第\\d+集\\$)?[^$]+)\\$(?<url>[^$]*)");

    private static final VideoType[] TYPES = {
            null, VideoType.MOVIE, VideoType.TV, VideoType.VARIETY, VideoType.ANIME, VideoType.HD,
            VideoType.FHD, VideoType.THREE_D, VideoType.MANDARIN, VideoType.MOVIE, VideoType.MOVIE,
            VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE,
            VideoType.MOVIE, VideoType.TV, VideoType.TV, VideoType.TV, VideoType.TV,
            VideoType.TV, VideoType.FOUR_K
    };

    public MovieHeavenSite() {
        super("Movie Heaven", "993dy.com");
    }

    @Override
    protected List<URI> getAllUris() {
        return getUrisById(1, 73911, id -> createUri0("/vod-detail-id-%d.html", id));
    }

    @Override
    protected Set<URI> searchItems(@Nonnull String keyword) {
        Document document;
        try {
            document = getDocument(builder0("/index.php")
                    .addParameter("m", "vod-search")
                    .addParameter("wd", keyword)
                    .addParameter("submit", "搜索影片"), true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Element ul = document.selectFirst("ul.img-list");
        return ul.select(TAG_LI).stream()
                .map(li -> createUri0(li.selectFirst(TAG_A).attr(ATTR_HREF)))
                .collect(Collectors.toSet());
    }

    @Override
    protected SimpleItem getItem(@Nonnull URI uri) throws NotFoundException {
        Document document = getDocument(new URIBuilder(uri), true);
        String title = document.title();
        if (TIP_TITLE.equals(title)) {
            throw new NotFoundException(document.selectFirst("h4.infotitle1").text());
        }

        SimpleItem item = new SimpleItem(uri.toString());
        item.setTitle(RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, title).group("title"));

        Map<String, Node> infos = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        Element div = document.selectFirst("div.info");
        for (Element element : div.select(TAG_SPAN)) {
            infos.put(element.text(), element.nextSibling());
        }
        Node node = infos.get("类型：");
        item.setType(TYPES[Integer.parseInt(RegexUtils.matchesOrElseThrow(TYPE_PATH_REGEX, node.attr(ATTR_HREF)).group("index"))]);
        node = infos.get("上映年代：");
        String text = ((TextNode) node).text();
        if (StringUtils.isNotBlank(text) && !UNKNOWN_YEAR.equals(text)) {
            int year = Integer.parseInt(text);
            if (year >= VideoConstants.FILM_START_YEAR && year <= Year.now().getValue()) {
                item.setYear(year);
            }
        }

        List<Resource> resources = new LinkedList<>();
        final String downUl = "ul.downurl";
        for (Element ul : document.select(downUl)) {
            String varUrls = ul.selectFirst(TAG_SCRIPT).html().strip().split("\n")[0].strip();
            String entries = RegexUtils.matchesOrElseThrow(VAR_URL_REGEX, varUrls).group("entries");
            entries = StringEscapeUtils.unescapeHtml4(entries);
            for (String entry : entries.split(SignEnum.HASH.toString())) {
                Matcher matcher = RegexUtils.matchesOrElseThrow(RESOURCE_REGEX, entry);
                String url = matcher.group("url");
                if (StringUtils.isBlank(url) || Thunder.EMPTY_LINK.equals(url)) {
                    continue;
                }
                resources.add(ResourceFactory.create(matcher.group("title"), url, () -> new UnknownResource(entry)));
            }
        }
        item.setResources(resources);

        return item;
    }
}
