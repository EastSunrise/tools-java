package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.common.ResourceUtil;
import wsg.tools.internet.resource.common.VideoTypeEnum;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.title.IdentifiedDetail;
import wsg.tools.internet.resource.entity.title.SimpleItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kingen
 * @see <a href="http://m.y80s.com">80s</a>
 * @since 2020/9/9
 */
@Slf4j
public class Y80sSite extends BaseResourceSite<SimpleItem, IdentifiedDetail> {

    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com(?<path>/(?<type>ju|movie|dm|trailer|mv|video|course|zy)/\\d+)");
    private static final Pattern DOUBAN_REVIEWS_REGEX = Pattern.compile("//movie.douban.com/subject/(\\d*) ?//?reviews");
    private static final String UNKNOWN_YEAR = "未知";
    private static final Map<String, VideoTypeEnum> TYPE_AKA = Map.of(
            "movie", VideoTypeEnum.MOVIE,
            "ju", VideoTypeEnum.TV,
            "dm", VideoTypeEnum.ANIME,
            "trailer", VideoTypeEnum.TRAILER,
            "mv", VideoTypeEnum.MV,
            "video", VideoTypeEnum.VIDEO,
            "course", VideoTypeEnum.COURSE
    );

    public Y80sSite() {
        super("80s", SchemeEnum.HTTP, "m.y80s.com", 0.1);
    }

    /**
     * Search and collect resources based on the given arguments.
     *
     * @param season current season, null for movie
     */
    public Set<AbstractResource> collect(String title, int year, @Nullable Integer season, @Nullable Long dbId) {
        VideoTypeEnum type = season == null ? VideoTypeEnum.MOVIE : VideoTypeEnum.TV;
        Set<AbstractResource> resources = new HashSet<>();
        for (SimpleItem item : search(title)) {
            String itemTitle = item.getTitle();
            IdentifiedDetail detail = find(item);
            Long providedId = detail.getDbId();
            if (dbId == null || providedId == null) {
                // todo classify anime to tv/movie
                if (type != item.getType()) {
                    continue;
                }
                if (!Objects.equals(year, item.getYear())) {
                    continue;
                }
                if (!isPossibleTitle(title, itemTitle, year, season)) {
                    continue;
                }
            } else if (!dbId.equals(providedId)) {
                continue;
            }
            // may include trailers, bloopers.
            log.info("Chosen title: {}", itemTitle);
            resources.addAll(detail.getResources());
        }
        return resources;
    }

    /**
     * Validate whether the title is one possible title of the given target.
     */
    private boolean isPossibleTitle(String target, String provided, int year, Integer season) {
        AssertUtils.requireNotBlank(target);
        if (StringUtils.isBlank(provided)) {
            return false;
        }

        return isPossibleSeason1(target, provided, year, season);
    }

    @Override
    protected final Set<SimpleItem> search(@Nonnull String keyword) {
        AssertUtils.requireNotBlank(keyword);
        List<BasicNameValuePair> params = Collections.singletonList(new BasicNameValuePair("keyword", keyword));
        Elements as;
        try {
            as = postDocument(builder0("/search"), params, true).select("a.list-group-item");
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Set<SimpleItem> items = new HashSet<>();
        for (Element a : as) {
            SimpleItem item = new SimpleItem();
            Matcher matcher = AssertUtils.matches(TITLE_HREF_REGEX, a.attr(ATTR_HREF));
            item.setPath(matcher.group("path"));
            String type = matcher.group("type");
            item.setType(Objects.requireNonNull(TYPE_AKA.get(type), "Can't recognize type from '" + type + "'"));
            Element small = a.selectFirst(TAG_SMALL);
            item.setTitle(small.previousSibling().outerHtml().strip());
            String text = small.text();
            item.setYear((StringUtils.isBlank(text) || UNKNOWN_YEAR.equals(text)) ? null : Integer.parseInt(text));
            items.add(item);
        }
        return items;
    }

    @Override
    protected final IdentifiedDetail find(@Nonnull SimpleItem item) {
        IdentifiedDetail detail = new IdentifiedDetail();
        Document document;
        try {
            document = getDocument(builder0(item.getPath()), true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        String idStr = AssertUtils.find(DOUBAN_REVIEWS_REGEX, document.selectFirst("p.col-xs-6").html()).group(1);
        if ("".equals(idStr)) {
            detail.setDbId(null);
        } else {
            long id = Long.parseLong(idStr);
            detail.setDbId(id <= 1000000 ? null : id);
        }
        detail.setResources(document.select(TAG_TR).stream()
                .map(tr -> tr.selectFirst(TAG_A))
                .map(a -> ResourceUtil.classifyUrl(a.attr(ATTR_HREF), a.text().strip()))
                .collect(Collectors.toSet()));
        return detail;
    }
}
