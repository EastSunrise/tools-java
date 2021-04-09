package wsg.tools.internet.movie.resource;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.RequestWrapper;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.WrappedStringResponseHandler;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.Thunder;
import wsg.tools.internet.download.support.InvalidResourceException;
import wsg.tools.internet.download.support.LinkFactory;

/**
 * @author Kingen
 * @see <a href="https://www.xleimi.com/">XLM</a>
 * @since 2020/12/2
 */
@ConcreteSite
public final class XlmSite extends AbstractListResourceSite<XlmItem> {

    private static final Range<Integer> EXCEPTS = Range.between(31588, 32581);
    private static final String DOWNLOAD_ASP = "/download.asp";

    public XlmSite() {
        super("Xlm", httpsHost("xleimi.com"), new WrappedStringResponseHandler(Constants.GBK));
    }

    /**
     * Returns the repository of all the items from 1 to {@link #latest()} <strong>except those in
     * {@link #EXCEPTS}</strong>.
     */
    @Override
    @Nonnull
    public ListRepository<Integer, XlmItem> getRepository() throws OtherResponseException {
        IntStream stream = IntStream.rangeClosed(1, latest())
            .filter(i -> !EXCEPTS.contains(i));
        return Repositories.list(this, stream.boxed().collect(Collectors.toList()));
    }

    /**
     * @see <a href="https://www.xleimi.com/">Last Update</a>
     */
    public int latest() throws OtherResponseException {
        Document document = findDocument(httpGet(""), t -> true);
        Elements as = document.selectFirst(".newdy").select(CssSelectors.TAG_A);
        int max = 1;
        for (Element a : as) {
            String href = a.attr(CssSelectors.ATTR_HREF);
            String id = RegexUtils.matchesOrElseThrow(Lazy.ITEM_HREF_REGEX, href).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    @Nonnull
    @Override
    public XlmItem findById(@Nonnull Integer id) throws NotFoundException, OtherResponseException {
        RequestWrapper wrapper = httpGet("/dy/k%d.html", id);
        Document document = getDocument(wrapper, doc -> getNext(doc) == null);

        Element last = document.selectFirst("div.conpath").select(CssSelectors.TAG_A).last();
        String columnHref = last.attr(CssSelectors.ATTR_HREF);
        Matcher columnMatcher = RegexUtils.matchesOrElseThrow(Lazy.COLUMN_HREF_REGEX, columnHref);
        int code = Integer.parseInt(columnMatcher.group("c"));
        XlmColumn column = EnumUtilExt.valueOfCode(XlmColumn.class, code);
        Element info = document.selectFirst(".info");
        Element font = info.selectFirst(".time").selectFirst(CssSelectors.TAG_FONT);
        LocalDateTime releaseTime = LocalDateTime.parse(font.text(), Lazy.FORMATTER);
        URL source = NetUtils.toURL(wrapper.getUri());
        String title = ((TextNode) last.nextSibling()).text().strip();
        XlmItem item = new XlmItem(column, source, id, title, releaseTime);
        Element image = document.selectFirst(".bodytxt").selectFirst(CssSelectors.TAG_IMG);
        if (image != null) {
            item.setCover(NetUtils.createURL(image.attr(CssSelectors.ATTR_SRC)));
        }
        item.setNext(getNext(document));

        Element downs = document.selectFirst("#downs");
        List<Link> resources = new ArrayList<>();
        List<InvalidResourceException> exceptions = new ArrayList<>();
        for (Element a : downs.select(CssSelectors.TAG_A)) {
            String href = a.attr(CssSelectors.ATTR_HREF).strip();
            if (href.endsWith("=/") || href.endsWith("=v")) {
                href = StringUtils.stripEnd(href, "/v");
            }
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href) || href
                .startsWith(DOWNLOAD_ASP)) {
                continue;
            }
            String t = a.text().strip();
            try {
                resources.add(LinkFactory
                    .create(t, href, Constants.GBK, () -> LinkFactory.getPassword(t)));
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
        return Integer.parseInt(RegexUtils.matchesOrElseThrow(Lazy.ITEM_HREF_REGEX, next.attr(
            CssSelectors.ATTR_HREF)).group("id"));
    }

    private static class Lazy {

        private static final Pattern ITEM_HREF_REGEX = Pattern.compile("/dy/k(?<id>\\d+)\\.html");
        private static final Pattern COLUMN_HREF_REGEX;
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("yyyy/M/d H:mm:ss");

        static {
            String columns = Arrays.stream(XlmColumn.values()).map(IntCodeSupplier::getCode)
                .map(String::valueOf).collect(Collectors.joining("|"));
            COLUMN_HREF_REGEX = Pattern.compile("/lanmu/xz(?<c>" + columns + ").html");
        }
    }
}
