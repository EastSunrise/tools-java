package wsg.tools.internet.resource.movie;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.Repositories;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.IntIndicesRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.LinkFactory;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.impl.Thunder;
import wsg.tools.internet.movie.common.VideoConstants;

/**
 * @author Kingen
 * @see <a href="https://www.xunleicang.in/">XunLeiCang</a>
 * @since 2020/9/9
 */
@Slf4j
public final class XlcSite extends BaseSite implements Repository<Integer, XlcItem> {

    private static final Pattern ITEM_TITLE_REGEX = Pattern.compile("(?<title>.*)_迅雷下载_高清电影_迅雷仓");
    private static final Pattern YEAR_REGEX = Pattern.compile("\\((?<y>\\d+)\\)");
    private static final Pattern ITEM_HREF_REGEX = Pattern
        .compile("/vod-read-id-(?<id>\\d+)\\.html");
    private static final Pattern TYPE_PATH_REGEX;

    static {
        String types = Arrays.stream(XlcType.values()).map(IntCodeSupplier::getCode)
            .map(String::valueOf).collect(Collectors.joining("|"));
        TYPE_PATH_REGEX = Pattern.compile("/vod-show-id-(?<id>" + types + ")\\.html");
    }

    public XlcSite() {
        super("XLC", new BasicHttpSession("xunleicang.in"));
    }

    /**
     * Returns the repository of all items from 1 to {@link #max()}. <strong>About 8% of the items
     * are not found.</strong>
     */
    public IntIndicesRepository<XlcItem> getRepository() throws HttpResponseException {
        return Repositories.rangeClosed(this, 1, max());
    }

    /**
     * @see <a href="https://www.xunleicang.in/ajax-show-id-new.html">Last Update</a>
     */
    @Nonnull
    public Integer max() throws HttpResponseException {
        RequestBuilder builder = builder0("/ajax-show-id-new.html");
        Document document = getDocument(builder, SnapshotStrategy.always());
        Elements as = document.selectFirst("ul.f6").select(CssSelectors.TAG_A);
        int max = 1;
        for (Element a : as) {
            String href = a.attr(CssSelectors.ATTR_HREF);
            String id = RegexUtils.matchesOrElseThrow(ITEM_HREF_REGEX, href).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    @Override
    public XlcItem findById(@Nonnull Integer id) throws HttpResponseException {
        RequestBuilder builder = builder0("/vod-read-id-%d.html", id);
        Document document = getDocument(builder, SnapshotStrategy.never());

        Element pLeft = document.selectFirst(".pleft");
        Map<String, Node> info = new HashMap<>(8);
        Elements elements = pLeft.selectFirst(".moviecont").select(CssSelectors.TAG_STRONG);
        for (Element strong : elements) {
            MapUtilsExt.putIfAbsent(info, strong.text(), strong.nextSibling());
        }
        String dateStr = ((TextNode) info.get("更新时间：")).text();
        LocalDate updateDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        Element header = pLeft.selectFirst(CssSelectors.TAG_H3).select(CssSelectors.TAG_A).last();
        String typeHref = header.previousElementSibling().attr(CssSelectors.ATTR_HREF);
        Matcher matcher = RegexUtils.matchesOrElseThrow(TYPE_PATH_REGEX, typeHref);
        int typeId = Integer.parseInt(matcher.group("id"));
        XlcType type = EnumUtilExt.deserializeCode(typeId, XlcType.class);
        String state = ((TextNode) info.get("状态：")).text();
        XlcItem item = new XlcItem(id, builder.toString(), updateDate, type, state);

        Matcher titleMatcher = RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, document.title());
        item.setTitle(titleMatcher.group(CssSelectors.ATTR_TITLE));
        Element font = header.selectFirst(CssSelectors.TAG_FONT);
        if (font != null) {
            Matcher yearMatcher = RegexUtils.matchesOrElseThrow(YEAR_REGEX, font.text());
            int year = Integer.parseInt(yearMatcher.group("y"));
            if (year >= VideoConstants.MOVIE_START_YEAR && year <= Year.now().getValue()) {
                item.setYear(year);
            }
        }

        List<AbstractLink> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        Elements lis = document.select("ul.down-list").select("li.item");
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_A);
            String href = a.attr(CssSelectors.ATTR_HREF);
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href)) {
                continue;
            }
            String title = a.text().strip();
            try {
                resources.add(
                    LinkFactory.create(title, href, () -> LinkFactory.getPassword(title, href)));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setLinks(resources);
        item.setExceptions(exceptions);
        return item;
    }
}
