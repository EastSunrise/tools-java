package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.common.ResourceUtil;
import wsg.tools.internet.resource.common.VideoTypeEnum;
import wsg.tools.internet.resource.entity.CollectResult;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.title.BaseDetail;
import wsg.tools.internet.resource.entity.title.SimpleItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://aixiaoju.com">AXJ</a>
 * @since 2020/9/9
 */
@Slf4j
public class XlcSite extends BaseResourceSite<SimpleItem, BaseDetail> {

    private static final Pattern POSSIBLE_TITLE_REGEX =
            Pattern.compile("(\\u005B[^\\u005B\\u005D]+\\u005D)?(?<title>[^\\u005B\\u005D]+)(\\u005B[^\\u005B\\u005D]+\\u005D([^\\u005B\\u005D]+)?)?");
    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("(https://www\\.(xunleicang\\.in|xlc2020\\.com))?(/vod-read-id-\\d+.html)");
    private static final int TYPE_INFO_START = "类型: ".length();
    private static final Map<String, VideoTypeEnum> TYPE_AKA = new HashMap<>(20);

    static {
        TYPE_AKA.put("国语配音", VideoTypeEnum.UNKNOWN);
        TYPE_AKA.put("喜剧片", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("剧情片", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("动作片", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("爱情片", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("恐怖片", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("战争片", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("科幻片", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("综艺片", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("其它片", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("1080P", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("4K", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("3D电影", VideoTypeEnum.MOVIE);
        TYPE_AKA.put("大陆剧", VideoTypeEnum.TV);
        TYPE_AKA.put("日韩剧", VideoTypeEnum.TV);
        TYPE_AKA.put("欧美剧", VideoTypeEnum.TV);
        TYPE_AKA.put("港台剧", VideoTypeEnum.TV);
        TYPE_AKA.put("动画片", VideoTypeEnum.ANIME);
    }

    public XlcSite() {
        super("XLC", "www.xunleicang.in", 0.1);
    }

    /**
     * Search and collect resources based on the given arguments.
     *
     * @param season current season, null for movie
     */
    public CollectResult<SimpleItem> collect(String title, int year, @Nullable Integer season) {
        VideoTypeEnum type = season == null ? VideoTypeEnum.MOVIE : VideoTypeEnum.TV;
        CollectResult<SimpleItem> result = new CollectResult<>();
        for (SimpleItem item : search(title)) {
            // todo classify anime/unknown to tv/movie
            if (type != item.getType() || !Objects.equals(year, item.getYear())
                    || !isPossibleTitle(title, item.getTitle(), year, season)) {
                result.exclude(item);
                continue;
            }
            BaseDetail detail = find(item);
            result.include(detail.getResources());
        }
        return result;
    }

    /**
     * Validate whether the title is one possible title of the given target.
     */
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
    protected final Set<SimpleItem> search(@Nonnull String keyword) {
        List<BasicNameValuePair> params = Collections.singletonList(new BasicNameValuePair("wd", keyword));
        Document document;
        try {
            document = postDocument(builder0("/vod-search"), params, true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Set<SimpleItem> items = new HashSet<>();
        String movList = "div.movList4";
        for (Element div : document.select(movList)) {
            Element h3 = div.selectFirst(TAG_H3);
            Element a = h3.selectFirst(TAG_A);
            Matcher matcher = TITLE_HREF_REGEX.matcher(a.attr(ATTR_HREF));
            if (!matcher.matches()) {
                continue;
            }
            SimpleItem item = new SimpleItem();
            item.setPath(matcher.group(3));
            String typeInfo = div.selectFirst("li.playactor").nextElementSibling().text();
            String type = typeInfo.substring(TYPE_INFO_START);
            item.setType(Objects.requireNonNull(TYPE_AKA.get(type), "Can't recognize type from '" + type + "'"));
            item.setTitle(a.text().strip());
            int year = Integer.parseInt(a.nextElementSibling().text());
            item.setYear(year == 0 ? null : year);
            items.add(item);
        }
        return items;
    }

    @Override
    protected final BaseDetail find(@Nonnull SimpleItem item) {
        BaseDetail detail = new BaseDetail();
        Set<AbstractResource> resources = new HashSet<>();
        String downList = "ul.down-list";
        final String itemCss = "li.item";
        try {
            for (Element ul : getDocument(builder0(item.getPath()), true).select(downList)) {
                ul.select(itemCss).stream().map(li -> li.selectFirst(TAG_A)).forEach(a -> {
                    String href = a.attr(ATTR_HREF);
                    AbstractResource resource = ResourceUtil.classifyUrl(href);
                    resource.setTitle(a.text().strip());
                    resources.add(resource);
                });
            }
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        detail.setResources(resources);
        return detail;
    }
}
