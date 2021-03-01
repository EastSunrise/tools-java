package wsg.tools.internet.resource.site;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.IntRangeRepositoryImpl;
import wsg.tools.internet.base.RequestBuilder;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.impl.ResourceFactory;
import wsg.tools.internet.resource.item.VideoType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://www.xleimi.com/">XLM</a>
 * @since 2020/12/2
 */
public class XlmSite extends IntRangeRepositoryImpl<XlmItem> {

    private static final String DOWNLOAD_ASP = "/download.asp";
    private static final Pattern ITEM_HREF_REGEX = Pattern.compile("/dy/k(?<id>\\d+)\\.html");
    private static final Pattern ITEM_TITLE_REGEX = Pattern.compile("《(?<title>[^《》]*(《[^《》]+》)?[^《》]*)》\\S+\\1\\S+");
    private static final Pattern TYPE_HREF_REGEX = Pattern.compile("/lanmu/xz(?<i>\\d+).html");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/M/d H:mm:ss");
    private static final VideoType[] TYPES = new VideoType[]{
            null, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.TV,
            VideoType.TV, VideoType.TV, VideoType.TV, VideoType.VARIETY, VideoType.ANIME,
            VideoType.THREE_D, VideoType.TV
    };

    private static XlmSite instance;

    private XlmSite() {
        super("Xlm", "xleimi.com", new AbstractResponseHandler<>() {
            @Override
            public String handleEntity(HttpEntity entity) throws IOException {
                return EntityUtils.toString(entity, Constants.GBK);
            }
        });
    }

    public static XlmSite getInstance() {
        if (instance == null) {
            instance = new XlmSite();
        }
        return instance;
    }

    /**
     * @see <a href="https://www.xleimi.com/new.html">Last Update</a>
     */
    @Override
    protected int max() throws HttpResponseException {
        Document document = getDocument(builder0("/new.html"), SnapshotStrategy.ALWAYS_UPDATE);
        Elements tits = document.select(".tit");
        int max = 1;
        for (Element tit : tits) {
            String id = RegexUtils.matchesOrElseThrow(ITEM_HREF_REGEX, tit.attr(CssSelector.ATTR_HREF)).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    @Override
    protected XlmItem getItem(int id) throws HttpResponseException {
        RequestBuilder builder = builder0("/dy/k%d.html", id);
        Document document = getDocument(builder, SnapshotStrategy.NEVER_UPDATE);

        String typeHref = document.selectFirst("div.conpath").select(CssSelector.TAG_A).get(1).attr(CssSelector.ATTR_HREF);
        String i = RegexUtils.matchesOrElseThrow(TYPE_HREF_REGEX, typeHref).group("i");
        VideoType type = Objects.requireNonNull(TYPES[Integer.parseInt(i)]);
        LocalDateTime releaseTime = LocalDateTime.parse(document.selectFirst(".info").selectFirst(".time").selectFirst(CssSelector.TAG_FONT).text(), FORMATTER);
        XlmItem item = new XlmItem(id, builder.toString(), releaseTime, type);
        item.setTitle(RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, document.title()).group("title"));

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
}
