package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.common.ResourceUtil;
import wsg.tools.internet.resource.common.VideoTypeEnum;
import wsg.tools.internet.resource.entity.CollectResult;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.title.BaseItem;
import wsg.tools.internet.resource.entity.title.SimpleDetail;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public class MovieHeavenSite extends BaseResourceSite<BaseItem, SimpleDetail> {

    private static final Pattern POSSIBLE_TITLE_REGEX =
            Pattern.compile("(\\u005B[^\\u005B\\u005D]+\\u005D)?(?<title>[^\\u005B\\u005D]+)(\\u005B[^\\u005B\\u005D]+\\u005D([^\\u005B\\u005D]+)?)?");
    private static final String UNKNOWN_YEAR = "未知";
    private static final Pattern TYPE_PATH_REGEX = Pattern.compile("/vod-type-id-(?<id>\\d+)-pg-1.html");
    private static final VideoTypeEnum[] TYPES = {
            null, VideoTypeEnum.MOVIE, VideoTypeEnum.TV, VideoTypeEnum.TV, VideoTypeEnum.ANIME, VideoTypeEnum.MOVIE,
            VideoTypeEnum.MOVIE, VideoTypeEnum.MOVIE, VideoTypeEnum.UNKNOWN, VideoTypeEnum.MOVIE, VideoTypeEnum.MOVIE,
            VideoTypeEnum.MOVIE, VideoTypeEnum.MOVIE, VideoTypeEnum.MOVIE, VideoTypeEnum.MOVIE, VideoTypeEnum.MOVIE,
            VideoTypeEnum.MOVIE, VideoTypeEnum.TV, VideoTypeEnum.TV, VideoTypeEnum.TV, VideoTypeEnum.TV,
            VideoTypeEnum.TV, VideoTypeEnum.MOVIE
    };

    public MovieHeavenSite() {
        super("Movie Heaven", "993dy.com");
    }

    /**
     * Search and collect resources based on the given arguments.
     *
     * @param season current season, null for movie
     */
    public CollectResult<BaseItem> collect(String title, int year, @Nullable Integer season) {
        VideoTypeEnum type = season == null ? VideoTypeEnum.MOVIE : VideoTypeEnum.TV;
        CollectResult<BaseItem> result = new CollectResult<>();
        for (BaseItem item : search(title)) {
            SimpleDetail detail = find(item);
            if (type != detail.getType() || !Objects.equals(year, detail.getYear())
                    || !isPossibleTitle(title, item.getTitle(), year, season)) {
                result.exclude(item);
                continue;
            }
            result.include(detail.getResources());
        }
        return result;
    }

    private boolean isPossibleTitle(String target, String provided, int year, Integer season) {
        AssertUtils.requireNotBlank(target);
        if (StringUtils.isBlank(provided)) {
            return false;
        }

        if (season == null) {
            Matcher matcher = AssertUtils.matches(POSSIBLE_TITLE_REGEX, provided);
            provided = matcher.group("title");
        }

        return isPossibleSeason1(target, provided, year, season);
    }

    @Override
    protected Set<BaseItem> search(@Nonnull String keyword) {
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
        return ul.select(TAG_LI).stream().map(li -> {
            Element a = li.selectFirst(TAG_A);
            BaseItem item = new BaseItem();
            item.setPath(a.attr(ATTR_HREF));
            item.setTitle(a.attr(ATTR_TITLE));
            return item;
        }).collect(Collectors.toSet());
    }

    @Override
    protected SimpleDetail find(@Nonnull BaseItem item) {
        SimpleDetail detail = new SimpleDetail();
        Document document;
        try {
            document = getDocument(builder0(item.getPath()), true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Element div = document.selectFirst("div.info");
        Map<String, Node> info = new HashMap<>(6);
        for (Element span : div.select(TAG_SPAN)) {
            info.put(span.text(), span.nextSibling());
        }
        Node node = info.get("上映年代：");
        if (node != null) {
            String text = ((TextNode) node).text();
            detail.setYear((StringUtils.isBlank(text) || UNKNOWN_YEAR.equals(text)) ? null : Integer.parseInt(text));
        }
        node = info.get("类型：");
        if (node != null) {
            detail.setType(TYPES[Integer.parseInt(AssertUtils.matches(TYPE_PATH_REGEX, node.attr(ATTR_HREF)).group("id"))]);
        }

        Set<AbstractResource> resources = new HashSet<>(Constants.DEFAULT_MAP_CAPACITY);
        final String downUl = "ul.downurl";
        for (Element ul : document.select(downUl)) {
            ul.select(TAG_LI).forEach(li -> resources.add(ResourceUtil.classifyUrl(li.selectFirst("input").val(), li.id())));
        }
        detail.setResources(resources);
        return detail;
    }
}
