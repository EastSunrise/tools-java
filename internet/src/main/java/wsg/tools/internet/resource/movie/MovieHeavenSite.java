package wsg.tools.internet.resource.movie;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.IntRangeIdentifiedRepositoryImpl;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.IntRangeIdentifiedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.UnexpectedException;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.LinkFactory;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.impl.Thunder;
import wsg.tools.internet.movie.common.VideoConstants;
import wsg.tools.internet.resource.common.VideoType;

/**
 * @author Kingen
 * @see <a href="https://www.993dy.com/">Movie Heaven</a>
 * @since 2020/10/18
 */
@Slf4j
public final class MovieHeavenSite extends BaseSite
    implements Repository<Integer, MovieHeavenItem> {

    private static final String TIP_TITLE = "系统提示";
    private static final Pattern ITEM_TITLE_REGEX = Pattern
        .compile("《(?<title>.+)》迅雷下载_(BT种子磁力|全集|最新一期)下载 - LOL电影天堂");
    private static final Pattern ITEM_HREF_REGEX = Pattern
        .compile("/vod-detail-id-(?<id>\\d+)\\.html");
    private static final Pattern TYPE_PATH_REGEX = Pattern
        .compile("/vod-type-id-(?<index>\\d+)-pg-1\\.html");
    private static final String UNKNOWN_YEAR = "未知";
    private static final Pattern VAR_URL_REGEX = Pattern
        .compile("var downurls=\"(?<entries>.*)#\";");
    private static final Pattern RESOURCE_REGEX = Pattern
        .compile("(?<title>(第\\d+集\\$)?[^$]+)\\$(?<url>[^$]*)");
    private static final String ILLEGAL_ARGUMENT = "您的提交带有不合法参数,谢谢合作!";
    private static final String XUNLEI = "xunlei";
    private static final String URL_SEPARATOR = "#";
    private static final VideoType[] TYPES = {
        null, VideoType.MOVIE, VideoType.SERIES, VideoType.VARIETY, VideoType.ANIME, VideoType.HD,
        VideoType.FHD, VideoType.THREE_D, VideoType.MANDARIN, VideoType.MOVIE, VideoType.MOVIE,
        VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE,
        VideoType.MOVIE, VideoType.SERIES, VideoType.SERIES, VideoType.SERIES, VideoType.SERIES,
        VideoType.SERIES, VideoType.FOUR_K
    };

    public MovieHeavenSite() {
        super("Movie Heaven", new BasicHttpSession("993dy.com"), new AbstractResponseHandler<>() {
            @Override
            public String handleEntity(HttpEntity entity) throws IOException {
                String content = EntityUtils.toString(entity);
                if (StringUtils.contains(content, ILLEGAL_ARGUMENT)) {
                    throw new HttpResponseException(HttpStatus.SC_FORBIDDEN, ILLEGAL_ARGUMENT);
                }
                return content;
            }
        });
    }

    public IntRangeIdentifiedRepository<MovieHeavenItem> getRepository() {
        return new IntRangeIdentifiedRepositoryImpl<>(this, max());
    }

    /**
     * @see <a href="https://www.993dy.com/">Home</a>
     */
    public int max() {
        Document document;
        try {
            document = getDocument(builder0("/"), SnapshotStrategy.always());
        } catch (HttpResponseException e) {
            throw new UnexpectedException(e);
        }
        Elements lis = document.selectFirst("div.newbox").select(CssSelectors.TAG_LI);
        int max = 1;
        for (Element li : lis) {
            String id = RegexUtils
                .matchesOrElseThrow(ITEM_HREF_REGEX, li.selectFirst(CssSelectors.TAG_A).attr(
                    CssSelectors.ATTR_HREF)).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    @Override
    public MovieHeavenItem findById(@Nonnull Integer id) throws HttpResponseException {
        RequestBuilder builder = builder0("/vod-detail-id-%d.html", id);
        Document document = getDocument(builder, SnapshotStrategy.never());
        String title = document.title();
        if (TIP_TITLE.equals(title)) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND,
                document.selectFirst("h4.infotitle1").text());
        }

        Map<String, Element> info = document.selectFirst("div.info").select(CssSelectors.TAG_SPAN)
            .stream().collect(Collectors.toMap(Element::text, e -> e));
        String href = info.get("类型：").nextElementSibling().attr(CssSelectors.ATTR_HREF);
        VideoType type = TYPES[Integer
            .parseInt(RegexUtils.matchesOrElseThrow(TYPE_PATH_REGEX, href).group("index"))];
        LocalDate addDate = LocalDate.parse(((TextNode) info.get("上架时间：").nextSibling()).text(),
            DateTimeFormatter.ISO_LOCAL_DATE);
        MovieHeavenItem item = new MovieHeavenItem(id, builder.toString(), type, addDate);

        item.setTitle(RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, title).group(
            CssSelectors.ATTR_TITLE));
        String text = ((TextNode) info.get("上映年代：").nextSibling()).text();
        if (StringUtils.isNotBlank(text) && !UNKNOWN_YEAR.equals(text)) {
            int year = Integer.parseInt(text);
            if (year >= VideoConstants.MOVIE_START_YEAR && year <= Year.now().getValue()) {
                item.setYear(year);
            }
        }
        Node node = info.get("状态：").nextSibling();
        if (node != null) {
            item.setState(((TextNode) node).text().strip());
        }

        List<AbstractLink> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        final String downUl = "ul.downurl";
        for (Element ul : document.select(downUl)) {
            String varUrls = ul.selectFirst(CssSelectors.TAG_SCRIPT).html().strip().split("\n")[0]
                .strip();
            String entries = RegexUtils.matchesOrElseThrow(VAR_URL_REGEX, varUrls).group("entries");
            entries = StringEscapeUtils.unescapeHtml4(entries);
            for (String entry : entries.split(URL_SEPARATOR)) {
                Matcher matcher = RegexUtils.matchesOrElseThrow(RESOURCE_REGEX, entry);
                String url = matcher.group("url");
                if (StringUtils.isBlank(url) || Thunder.EMPTY_LINK.equals(url)) {
                    continue;
                }
                String t = matcher.group(CssSelectors.ATTR_TITLE);
                if (XUNLEI.equals(url)) {
                    url = t;
                    t = XUNLEI;
                }
                try {
                    resources.add(LinkFactory.create(t, url, () -> LinkFactory.getPassword(title)));
                } catch (InvalidResourceException e) {
                    exceptions.add(e);
                }
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);
        return item;
    }
}
