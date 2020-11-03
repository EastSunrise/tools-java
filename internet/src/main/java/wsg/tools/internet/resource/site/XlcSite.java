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
import wsg.tools.internet.resource.common.VideoType;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.item.SimpleItem;
import wsg.tools.internet.resource.entity.resource.ResourceFactory;
import wsg.tools.internet.resource.entity.resource.base.Resource;
import wsg.tools.internet.resource.entity.resource.base.UnknownResource;

import javax.annotation.Nonnull;
import java.time.Year;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private static final Pattern YEAR_INFO_REGEX =
            Pattern.compile("(年.{0,2}代|年.{0,2}份|上.{0,2}映|出.{0,2}品|播出|发行|首映|推出|首播).{0,3}(</b>|</font>|</span>|<span [^<>]+>)?.{0,2}(?<year>\\d{4})");

    private static final Pattern ITEM_HREF_REGEX =
            Pattern.compile("(https://www\\.(xunleicang\\.in|xlc2020\\.com))?(?<path>/vod-read-id-\\d+.html)");
    private static final VideoType[] TYPES = {
            null, VideoType.MOVIE, VideoType.TV, VideoType.ANIME, VideoType.TV, VideoType.MOVIE,
            VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE,
            VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.TV,
            VideoType.TV, VideoType.TV, VideoType.TV, VideoType.TV, VideoType.MOVIE,
            VideoType.MOVIE
    };

    public XlcSite() {
        super("XLC", "www.xunleicang.in", 0.1);
    }

    @Override
    public Set<SimpleItem> findAll() {
        return IntStream.range(1, 43034).mapToObj(id -> {
            try {
                return getItem(String.format("/vod-read-id-%d.html", id));
            } catch (NotFoundException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    protected final Set<String> searchItems(@Nonnull String keyword) {
        List<BasicNameValuePair> params = Collections.singletonList(new BasicNameValuePair("wd", keyword));
        Document document;
        try {
            document = postDocument(builder0("/vod-search"), params, true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Set<String> paths = new HashSet<>();
        String movList = "div.movList4";
        for (Element div : document.select(movList)) {
            Element h3 = div.selectFirst(TAG_H3);
            Element a = h3.selectFirst(TAG_A);
            Matcher matcher = ITEM_HREF_REGEX.matcher(a.attr(ATTR_HREF));
            if (!matcher.matches()) {
                continue;
            }
            paths.add(matcher.group("path"));
        }
        return paths;
    }

    @Override
    protected final SimpleItem getItem(@Nonnull String path) throws NotFoundException {
        URIBuilder builder = builder0(path);
        Document document = getDocument(builder, true);
        SimpleItem item = new SimpleItem();
        item.setUrl(builder.toString());
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
        if (item.getYear() == null) {
            String text = document.selectFirst("div.movie_story3").text();
            RegexUtils.ifFind(YEAR_INFO_REGEX, text, m -> item.setYear(Integer.parseInt(m.group("year"))));
        }

        final String downList = "ul.down-list";
        final String itemCss = "li.item";
        List<Resource> resources = new LinkedList<>();
        for (Element ul : document.select(downList)) {
            for (Element li : ul.select(itemCss)) {
                Element a = li.selectFirst(TAG_A);
                String href = a.attr(ATTR_HREF);
                if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href)) {
                    resources.add(ResourceFactory.create(a.text().strip(), href, () -> new UnknownResource(href)));
                }
            }
        }
        item.setResources(resources);

        return item;
    }
}
