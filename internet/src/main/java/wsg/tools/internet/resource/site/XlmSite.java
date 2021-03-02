package wsg.tools.internet.resource.site;

import lombok.Getter;
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
import wsg.tools.internet.base.BasicHttpSession;
import wsg.tools.internet.base.RecordIterator;
import wsg.tools.internet.base.RequestBuilder;
import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.IterableRepositoryImpl;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.common.WithoutNextDocument;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.impl.ResourceFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://www.xleimi.com/">XLM</a>
 * @since 2020/12/2
 */
public class XlmSite extends BaseSite implements Repository<Integer, XlmItem> {

    private static final String DOWNLOAD_ASP = "/download.asp";
    private static final Pattern ITEM_HREF_REGEX = Pattern.compile("/dy/k(?<id>\\d+)\\.html");
    private static final Pattern ITEM_TITLE_REGEX = Pattern.compile("《(?<title>[^《》]*(《[^《》]+》)?[^《》]*)》\\S+\\1\\S+");
    private static final Pattern TYPE_HREF_REGEX = Pattern.compile("/lanmu/xz(?<c>\\d+).html");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/M/d H:mm:ss");

    private static XlmSite instance;

    @Getter
    private final Map<XlmType, IterableRepository<XlmItem>> repositories;

    private XlmSite() {
        super("Xlm", new BasicHttpSession("xleimi.com"), new AbstractResponseHandler<>() {
            @Override
            public String handleEntity(HttpEntity entity) throws IOException {
                return EntityUtils.toString(entity, Constants.GBK);
            }
        });
        repositories = new HashMap<>(XlmType.values().length);
        for (XlmType type : XlmType.values()) {
            repositories.put(type, new IterableRepositoryImpl<>(this, type.getFirst()));
        }
    }

    public synchronized static XlmSite getInstance() {
        if (instance == null) {
            instance = new XlmSite();
        }
        return instance;
    }

    public RecordIterator<XlmItem> iterator(@Nonnull XlmType type) throws HttpResponseException {
        return repositories.get(type).iterator();
    }

    @Override
    public XlmItem findById(@Nonnull Integer id) throws HttpResponseException {
        RequestBuilder builder = builder0("/dy/k%d.html", id);
        Document document = getDocument(builder, new WithoutNextDocument<>(this::getNext));

        String typeHref = document.selectFirst("div.conpath").select(CssSelector.TAG_A).get(1).attr(CssSelector.ATTR_HREF);
        int code = Integer.parseInt(RegexUtils.matchesOrElseThrow(TYPE_HREF_REGEX, typeHref).group("c"));
        XlmType type = EnumUtilExt.deserializeCode(code, XlmType.class);
        LocalDateTime releaseTime = LocalDateTime.parse(document.selectFirst(".info").selectFirst(".time").selectFirst(CssSelector.TAG_FONT).text(), FORMATTER);
        XlmItem item = new XlmItem(id, builder.toString(), releaseTime, type);
        item.setTitle(RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, document.title()).group("title"));
        item.setNext(getNext(document));

        Element downs = document.selectFirst("#downs");
        List<AbstractResource> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        for (Element a : downs.select(CssSelector.TAG_A)) {
            String href = a.attr(CssSelector.ATTR_HREF).strip();
            if (href.endsWith("=/") || href.endsWith("=v")) {
                href = StringUtils.stripEnd(href, "/v");
            }
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href) || href.startsWith(DOWNLOAD_ASP)) {
                continue;
            }
            String title = a.text().strip();
            try {
                resources.add(ResourceFactory.create(title, href, Constants.GBK, () -> ResourceFactory.getPassword(title)));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);
        return item;
    }

    private Integer getNext(Document document) {
        Elements spans = document.selectFirst("div.turn").select(CssSelector.TAG_SPAN);
        AssertUtils.requireEquals(spans.size(), 2);
        Element next = spans.get(0).selectFirst(CssSelector.TAG_A);
        if (next == null) {
            return null;
        }
        return Integer.parseInt(RegexUtils.matchesOrElseThrow(ITEM_HREF_REGEX, next.attr(CssSelector.ATTR_HREF)).group("id"));
    }
}
