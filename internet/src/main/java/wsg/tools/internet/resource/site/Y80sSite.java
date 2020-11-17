package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.common.VideoType;
import wsg.tools.internet.resource.entity.item.Y80sItem;
import wsg.tools.internet.resource.entity.resource.ResourceFactory;
import wsg.tools.internet.resource.entity.resource.base.Resource;
import wsg.tools.internet.resource.entity.resource.base.UnknownResource;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Kingen
 * @see <a href="http://m.y80s.com">80s</a>
 * @since 2020/9/9
 */
@Slf4j
public class Y80sSite extends BaseResourceSite<Y80sItem> {

    private static final Map<String, VideoType> TYPE_AKA = Map.of(
            "movie", VideoType.MOVIE,
            "ju", VideoType.TV,
            "zy", VideoType.VARIETY,
            "dm", VideoType.ANIME,
            "trailer", VideoType.TRAILER,
            "mv", VideoType.MV,
            "video", VideoType.VIDEO,
            "course", VideoType.COURSE
    );
    private static final Pattern ITEM_PATH_REGEX =
            Pattern.compile("(?<path>/(?<type>" + String.join("|", TYPE_AKA.keySet()) + ")/\\d+)");
    private static final Pattern ITEM_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com" + ITEM_PATH_REGEX.pattern());
    private static final Pattern DOUBAN_REVIEWS_REGEX = Pattern.compile("//movie.douban.com/subject/(\\d*)");
    private static final Pattern ITEM_TITLE_REGEX = Pattern.compile("(?<title>[^()]+)\\((?<year>\\d{4})\\)[^()]+");

    public Y80sSite() {
        super("80s", SchemeEnum.HTTP, "m.y80s.com", 0.1);
    }

    @Override
    public Set<Y80sItem> findAll() {
        return IntStream.range(1, 43034).mapToObj(id -> {
            try {
                return getItem(builder0("/movie/%d", id));
            } catch (NotFoundException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    protected final Set<URIBuilder> searchItems(@Nonnull String keyword) {
        AssertUtils.requireNotBlank(keyword);
        List<BasicNameValuePair> params = Collections.singletonList(new BasicNameValuePair("keyword", keyword));
        Elements as;
        try {
            as = postDocument(builder0("/search"), params, true).select("a.list-group-item");
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        return as.stream()
                .map(a -> RegexUtils.matchesOrElseThrow(ITEM_HREF_REGEX, a.attr(ATTR_HREF)).group("path"))
                .map(this::builder0)
                .collect(Collectors.toSet());
    }

    /**
     * todo
     */
    @Override
    protected final Y80sItem getItem(@Nonnull URIBuilder builder) throws NotFoundException {
        Document document = getDocument(builder, true);
        if (document.childNodes().size() == 1) {
            throw new NotFoundException("Target page is empty.");
        }
        Y80sItem item = new Y80sItem();

        String title = document.title();
        Set<Resource> resources = document.select(TAG_TR).stream()
                .map(tr -> tr.selectFirst(TAG_A))
                .map(a -> {
                    String href = a.attr(ATTR_HREF);
                    return ResourceFactory.create(a.text().strip(), href, () -> new UnknownResource(href));
                })
                .collect(Collectors.toSet());

        String idStr = RegexUtils.findOrElseThrow(DOUBAN_REVIEWS_REGEX, document.selectFirst("p.col-xs-6").html()).group(1);
        if ("".equals(idStr)) {
            item.setDbId(null);
        } else {
            long id = Long.parseLong(idStr);
            item.setDbId(id <= 1000000 ? null : id);
        }

        return item;
    }
}
