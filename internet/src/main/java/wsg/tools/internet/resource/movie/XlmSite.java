package wsg.tools.internet.resource.movie;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.Repositories;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.impl.WithoutNextDocument;
import wsg.tools.internet.base.intf.IntIdentifiedRepository;
import wsg.tools.internet.base.intf.LinkedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.StringResponseHandler;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.LinkFactory;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.impl.Thunder;

/**
 * @author Kingen
 * @see <a href="https://www.xleimi.com/">XLM</a>
 * @since 2020/12/2
 */
public final class XlmSite extends BaseSite implements Repository<Integer, XlmItem> {

    private static final Range<Integer> NOT_FOUNDS = Range.between(31588, 32581);
    private static final String DOWNLOAD_ASP = "/download.asp";
    private static final Pattern COLUMN_HREF_REGEX;
    private static final Pattern ITEM_HREF_REGEX = Pattern.compile("/dy/k(?<id>\\d+)\\.html");
    private static final Pattern ITEM_TITLE_REGEX = Pattern
        .compile("《(?<title>[^《》]*(《[^《》]+》)?[^《》]*)》\\S+\\1\\S+");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
        .ofPattern("yyyy/M/d H:mm:ss");

    static {
        String columns = Arrays.stream(XlmColumn.values()).map(IntCodeSupplier::getCode)
            .map(String::valueOf).collect(Collectors.joining("|"));
        COLUMN_HREF_REGEX = Pattern.compile("/lanmu/xz(?<c>" + columns + ").html");
    }

    public XlmSite() {
        super("Xlm", new BasicHttpSession("xleimi.com"), new StringResponseHandler(Constants.GBK));
    }

    /**
     * Returns the repository of all the items from 1 to {@link #max()} <strong>except those in
     * {@link #NOT_FOUNDS}</strong>.
     */
    public IntIdentifiedRepository<XlmItem> getRepository() throws HttpResponseException {
        return Repositories.rangeClosedExcept(this, 1, max(), NOT_FOUNDS);
    }

    /**
     * Returns the repository of the given type since very first one. <strong>May break
     * off</strong>.
     *
     * @see XlmColumn
     */
    public LinkedRepository<Integer, XlmItem> getRepository(@Nonnull XlmColumn type) {
        return Repositories.linked(this, type.first());
    }

    /**
     * @see <a href="https://www.xleimi.com/new.html">Last Update</a>
     */
    public int max() throws HttpResponseException {
        Document document = getDocument(builder0("/new.html"), SnapshotStrategy.always());
        Elements tits = document.select(".tit");
        int max = 1;
        for (Element tit : tits) {
            String href = tit.attr(CssSelectors.ATTR_HREF);
            String id = RegexUtils.matchesOrElseThrow(ITEM_HREF_REGEX, href).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    @Override
    public XlmItem findById(@Nonnull Integer id) throws HttpResponseException {
        RequestBuilder builder = builder0("/dy/k%d.html", id);
        Document document = getDocument(builder, new WithoutNextDocument<>(this::getNext));

        Elements heads = document.selectFirst("div.conpath").select(CssSelectors.TAG_A);
        String columnHref = heads.last().attr(CssSelectors.ATTR_HREF);
        Matcher columnMatcher = RegexUtils.matchesOrElseThrow(COLUMN_HREF_REGEX, columnHref);
        int code = Integer.parseInt(columnMatcher.group("c"));
        XlmColumn type = EnumUtilExt.deserializeCode(code, XlmColumn.class);
        Element info = document.selectFirst(".info");
        Element font = info.selectFirst(".time").selectFirst(CssSelectors.TAG_FONT);
        LocalDateTime releaseTime = LocalDateTime.parse(font.text(), FORMATTER);
        XlmItem item = new XlmItem(id, builder.toString(), releaseTime, type);
        Matcher matcher = RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, document.title());
        item.setTitle(matcher.group(CssSelectors.ATTR_TITLE));
        item.setNext(getNext(document));

        Element downs = document.selectFirst("#downs");
        List<AbstractLink> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        for (Element a : downs.select(CssSelectors.TAG_A)) {
            String href = a.attr(CssSelectors.ATTR_HREF).strip();
            if (href.endsWith("=/") || href.endsWith("=v")) {
                href = StringUtils.stripEnd(href, "/v");
            }
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href) || href
                .startsWith(DOWNLOAD_ASP)) {
                continue;
            }
            String title = a.text().strip();
            try {
                resources.add(LinkFactory
                    .create(title, href, Constants.GBK, () -> LinkFactory.getPassword(title)));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setLinks(resources);
        item.setExceptions(exceptions);
        return item;
    }

    private Integer getNext(Document document) {
        Elements spans = document.selectFirst("div.turn").select(CssSelectors.TAG_SPAN);
        AssertUtils.requireEquals(spans.size(), 2);
        Element next = spans.get(0).selectFirst(CssSelectors.TAG_A);
        if (next == null) {
            return null;
        }
        return Integer.parseInt(RegexUtils.matchesOrElseThrow(ITEM_HREF_REGEX, next.attr(
            CssSelectors.ATTR_HREF)).group("id"));
    }
}
