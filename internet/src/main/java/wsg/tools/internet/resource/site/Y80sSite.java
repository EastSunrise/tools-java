package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.entity.item.impl.Y80sItem;
import wsg.tools.internet.resource.entity.resource.ResourceFactory;
import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            "course", VideoType.COURSE,
            "weidianying", VideoType.MOVIE
    );
    private static final Pattern MOVIE_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com/movie/(?<id>\\d+)");
    private static final Pattern TYPE_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com/(?<type>movie|ju|zy|dm|trailer|mv|video|course|weidianying)/\\d+(-\\d){6}");
    private static final Pattern YEAR_REGEX = Pattern.compile("-?(?<year>\\d{4})(年|-\\d{2}-\\d{2})?|未知|\\d\\.\\d|\\d{5}|\\d{1,3}|");
    private static final Pattern DOUBAN_HREF_REGEX = Pattern.compile("//movie\\.douban\\.com/subject/((?<id>\\d+)( +|/|c|v|)|[^\\d].*?|)/reviews");

    public Y80sSite() {
        super("80s", SchemeEnum.HTTP, "m.y80s.com", 0.1);
    }

    @Override
    public List<Y80sItem> findAll() {
        return findAllByPathsIgnoreNotFound(getAllPaths(), this::getItem);
    }

    /**
     * @see <a href="http://m.y80s.com/movie/1-0-0-0-0-0-0">Last Update Movie</a>
     */
    private List<String> getAllPaths() {
        Document document;
        try {
            document = getDocument(builder0("/movie/1-0-0-0-0-0-0"), false);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Elements list = document.select(".list_mov");
        int max = 1;
        for (Element div : list) {
            String href = div.selectFirst(TAG_A).attr(ATTR_HREF);
            String id = RegexUtils.matchesOrElseThrow(MOVIE_HREF_REGEX, href).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return getPathsById(max, "/movie/%d", 6800, 10705, 21147, 24926);
    }

    private Y80sItem getItem(@Nonnull String path) throws NotFoundException {
        URIBuilder builder = builder0(path);
        Document document = getDocument(builder, true);
        if (document.childNodes().size() == 1) {
            throw new NotFoundException("Target page is empty.");
        }
        Y80sItem item = new Y80sItem(builder.toString());

        Elements lis = document.selectFirst("#path").select(TAG_LI);
        Matcher typeMatcher = RegexUtils.matchesOrElseThrow(TYPE_HREF_REGEX, lis.get(1).selectFirst(TAG_A).attr(ATTR_HREF));
        item.setType(Objects.requireNonNull(TYPE_AKA.get(typeMatcher.group("type"))));
        item.setTitle(lis.last().text().strip());

        Element main = document.selectFirst("#mainbody");
        Elements spans = main.select(".movie_attr");
        Map<String, Element> attributes = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (Element span : spans) {
            attributes.put(span.text().strip(), span);
        }
        Element span = attributes.get("年代：");
        if (span != null) {
            String year = RegexUtils.matchesOrElseThrow(YEAR_REGEX, span.nextElementSibling().text()).group("year");
            if (year != null) {
                item.setYear(Integer.parseInt(year));
            }
        }
        span = attributes.get("豆瓣评分：");
        if (span != null) {
            String doubanHref = span.nextElementSibling().nextElementSibling().attr(ATTR_HREF);
            String id = RegexUtils.matchesOrElseThrow(DOUBAN_HREF_REGEX, doubanHref).group("id");
            if (id != null) {
                item.setDbId(Long.parseLong(id));
            }
        }

        List<ValidResource> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        Elements dls = main.select("#dl-tab-panes").select("a.btn_dl");
        for (Element a : dls) {
            try {
                resources.add(ResourceFactory.create(a.text().strip(), a.attr(ATTR_HREF), null));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);
        return item;
    }
}
