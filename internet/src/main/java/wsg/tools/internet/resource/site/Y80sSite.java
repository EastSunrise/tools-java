package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.IntRangeRepositoryImpl;
import wsg.tools.internet.base.RequestBuilder;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.common.Scheme;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.impl.ResourceFactory;
import wsg.tools.internet.resource.item.VideoType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kingen
 * @see <a href="http://m.y80s.org">80s</a>
 * @since 2020/9/9
 */
@Slf4j
public class Y80sSite extends IntRangeRepositoryImpl<Y80sItem> {

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
    private static final String TYPE_JOINING_STR = StringUtils.join(TYPE_AKA.keySet(), "|");
    private static final Pattern MOVIE_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com/movie/(?<id>\\d+)");
    private static final Pattern TYPE_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com/(?<type>" + TYPE_JOINING_STR + ")/\\d+(-\\d){6}");
    private static final Pattern PLAY_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com/(" + TYPE_JOINING_STR + ")/\\d+/play-\\d+");
    private static final Pattern YEAR_REGEX = Pattern.compile("-?(?<year>\\d{4})(年|-\\d{2}-\\d{2})?|未知|\\d\\.\\d|\\d{5}|\\d{1,3}|");
    private static final Pattern DOUBAN_HREF_REGEX = Pattern.compile("//movie\\.douban\\.com/subject/((?<id>\\d+)( +|/|c|v|)|[^\\d].*?|)/reviews");
    private static Y80sSite instance;

    private Y80sSite() {
        super("80s", Scheme.HTTP, "y80s.org");
    }

    public static Y80sSite getInstance() {
        if (instance == null) {
            instance = new Y80sSite();
        }
        return instance;
    }

    /**
     * @see <a href="http://m.y80s.com/movie/1-0-0-0-0-0-0">Last Update Movie</a>
     */
    @Override
    protected int max() throws HttpResponseException {
        Document document = getDocument(builder("m", "/movie/1-0-0-0-0-0-0"), SnapshotStrategy.ALWAYS_UPDATE);
        Elements list = document.select(".list_mov");
        int max = 1;
        for (Element div : list) {
            String href = div.selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF);
            String id = RegexUtils.matchesOrElseThrow(MOVIE_HREF_REGEX, href).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    @Override
    protected Y80sItem getItem(int id) throws HttpResponseException {
        RequestBuilder builder = builder("m", "/movie/%d", id);
        Document document = getDocument(builder, SnapshotStrategy.NEVER_UPDATE);
        if (document.childNodes().size() == 1) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, "Target page is empty.");
        }

        Map<String, Element> info = document.select(".movie_attr").stream().collect(Collectors.toMap(Element::text, e -> e));
        Elements lis = document.selectFirst("#path").select(CssSelector.TAG_LI);
        Matcher typeMatcher = RegexUtils.matchesOrElseThrow(TYPE_HREF_REGEX, lis.get(1).selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF));
        VideoType type = Objects.requireNonNull(TYPE_AKA.get(typeMatcher.group("type")));
        Element span = info.get("资源更新：");
        LocalDate updateDate = LocalDate.parse(((TextNode) span.nextSibling()).text().strip(), DateTimeFormatter.ISO_LOCAL_DATE);
        Y80sItem item = new Y80sItem(id, builder.toString(), updateDate, type);

        item.setTitle(lis.last().text().strip());
        span = info.get("年代：");
        if (span != null) {
            String year = RegexUtils.matchesOrElseThrow(YEAR_REGEX, span.nextElementSibling().text()).group("year");
            if (year != null) {
                item.setYear(Integer.parseInt(year));
            }
        }
        span = info.get("豆瓣评分：");
        if (span != null) {
            String doubanHref = span.nextElementSibling().nextElementSibling().attr(CssSelector.ATTR_HREF);
            String dbId = RegexUtils.matchesOrElseThrow(DOUBAN_HREF_REGEX, doubanHref).group("id");
            if (dbId != null) {
                item.setDbId(Long.parseLong(dbId));
            }
        }

        List<AbstractResource> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        Elements trs = document.select("#dl-tab-panes").select(CssSelector.TAG_TR);
        for (Element tr : trs) {
            Element a = tr.selectFirst(CssSelector.TAG_A);
            String href = a.attr(CssSelector.ATTR_HREF);
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href)) {
                continue;
            }
            if (PLAY_HREF_REGEX.matcher(href).matches()) {
                href = Scheme.HTTP.toString() + Constants.URL_SCHEME_SEPARATOR + href;
            }
            String title = a.text().strip();
            try {
                resources.add(ResourceFactory.create(title, href, () -> ResourceFactory.getPassword(title)));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);
        return item;
    }
}
