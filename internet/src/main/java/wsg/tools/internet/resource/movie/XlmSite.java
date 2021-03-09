package wsg.tools.internet.resource.movie;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.LinkedRepositoryImpl;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.impl.WithoutNextDocument;
import wsg.tools.internet.base.intf.LinkedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.common.CssSelectors;
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

    private static final String DOWNLOAD_ASP = "/download.asp";
    private static final Pattern ITEM_HREF_REGEX = Pattern.compile("/dy/k(?<id>\\d+)\\.html");
    private static final Pattern ITEM_TITLE_REGEX = Pattern
        .compile("《(?<title>[^《》]*(《[^《》]+》)?[^《》]*)》\\S+\\1\\S+");
    private static final Pattern TYPE_HREF_REGEX = Pattern.compile("/lanmu/xz(?<c>\\d+).html");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
        .ofPattern("yyyy/M/d H:mm:ss");

    public XlmSite() {
        super("Xlm", new BasicHttpSession("xleimi.com"), new AbstractResponseHandler<>() {
            @Override
            public String handleEntity(HttpEntity entity) throws IOException {
                return EntityUtils.toString(entity, Constants.GBK);
            }
        });
    }

    public LinkedRepository<Integer, XlmItem> getRepository(@Nonnull XlmType type) {
        return new LinkedRepositoryImpl<>(this, type.first());
    }

    @Override
    public XlmItem findById(@Nonnull Integer id) throws HttpResponseException {
        RequestBuilder builder = builder0("/dy/k%d.html", id);
        Document document = getDocument(builder, new WithoutNextDocument<>(this::getNext));

        String typeHref = document.selectFirst("div.conpath").select(CssSelectors.TAG_A).get(1)
            .attr(
                CssSelectors.ATTR_HREF);
        int code = Integer
            .parseInt(RegexUtils.matchesOrElseThrow(TYPE_HREF_REGEX, typeHref).group("c"));
        XlmType type = EnumUtilExt.deserializeCode(code, XlmType.class);
        LocalDateTime releaseTime = LocalDateTime
            .parse(document.selectFirst(".info").selectFirst(".time").selectFirst(
                CssSelectors.TAG_FONT).text(), FORMATTER);
        XlmItem item = new XlmItem(id, builder.toString(), releaseTime, type);
        item.setTitle(RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, document.title()).group(
            CssSelectors.ATTR_TITLE));
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
        item.setResources(resources);
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
