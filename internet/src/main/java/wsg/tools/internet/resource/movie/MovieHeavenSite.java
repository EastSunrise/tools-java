package wsg.tools.internet.resource.movie;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.Repositories;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.IntIdentifiedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.StringResponseHandler;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.LinkFactory;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.impl.Thunder;
import wsg.tools.internet.movie.common.VideoConstants;

/**
 * @author Kingen
 * @see <a href="https://www.993dy.com/">Movie Heaven</a>
 * @see <a href="https://www.993vod.com/">Backup</a>
 * @since 2020/10/18
 */
@Slf4j
public final class MovieHeavenSite extends BaseSite implements
    Repository<Integer, MovieHeavenItem> {

    private static final String TIP_TITLE = "系统提示";
    private static final Pattern TYPE_HREF_REGEX;
    private static final Pattern ITEM_TITLE_REGEX = Pattern
        .compile("《(?<title>.+)》迅雷下载_(BT种子磁力|全集|最新一期)下载 - LOL电影天堂");
    private static final Pattern ITEM_HREF_REGEX = Pattern
        .compile("/vod-detail-id-(?<id>\\d+)\\.html");
    private static final String UNKNOWN_YEAR = "未知";
    private static final Pattern VAR_URL_REGEX = Pattern
        .compile("var downurls=\"(?<entries>.*)#\";");
    private static final Pattern RESOURCE_REGEX = Pattern
        .compile("(?<title>(第\\d+集\\$)?[^$]+)\\$(?<url>[^$]*)");
    private static final String ILLEGAL_ARGUMENT = "您的提交带有不合法参数,谢谢合作!";
    private static final String XUNLEI = "xunlei";
    private static final String URL_SEPARATOR = "#";

    static {
        String types = Arrays.stream(MovieHeavenType.values()).map(IntCodeSupplier::getCode)
            .map(String::valueOf).collect(Collectors.joining("|"));
        TYPE_HREF_REGEX = Pattern.compile("/vod-type-id-(?<id>" + types + ")-pg-1\\.html");
    }

    public MovieHeavenSite() {
        super("Movie Heaven", new BasicHttpSession("993vod.com"), new MovieHeaverResponseHandler());
    }

    /**
     * Returns the repository of all items from 1 to {@link #max()}. Only several items are not
     * found.
     */
    public IntIdentifiedRepository<MovieHeavenItem> getRepository()
        throws HttpResponseException {
        return Repositories.rangeClosed(this, 1, max());
    }

    /**
     * @see <a href="https://www.993dy.com/">Home</a>
     */
    public int max() throws HttpResponseException {
        Document document = getDocument(builder0(""), SnapshotStrategy.always());
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
            String message = document.selectFirst("h4.infotitle1").text();
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, message);
        }

        Map<String, Element> info = document.selectFirst("div.info").select(CssSelectors.TAG_SPAN)
            .stream().collect(Collectors.toMap(Element::text, e -> e));
        String typeHref = info.get("类型：").nextElementSibling().attr(CssSelectors.ATTR_HREF);
        Matcher typeMatcher = RegexUtils.matchesOrElseThrow(TYPE_HREF_REGEX, typeHref);
        int typeId = Integer.parseInt(typeMatcher.group("id"));
        MovieHeavenType type = EnumUtilExt.deserializeCode(typeId, MovieHeavenType.class);
        String timeText = ((TextNode) info.get("上架时间：").nextSibling()).text();
        LocalDate addDate = LocalDate.parse(timeText, DateTimeFormatter.ISO_LOCAL_DATE);
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
        item.setLinks(resources);
        item.setExceptions(exceptions);
        return item;
    }

    private static class MovieHeaverResponseHandler extends StringResponseHandler {

        @Override
        protected String handleContent(String content) throws HttpResponseException {
            if (StringUtils.contains(content, ILLEGAL_ARGUMENT)) {
                throw new HttpResponseException(HttpStatus.SC_FORBIDDEN, ILLEGAL_ARGUMENT);
            }
            return content;
        }
    }
}
