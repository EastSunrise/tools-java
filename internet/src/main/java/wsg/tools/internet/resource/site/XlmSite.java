package wsg.tools.internet.resource.site;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.entity.item.impl.SimpleItem;
import wsg.tools.internet.resource.entity.resource.ResourceFactory;
import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://www.xlmdytt.com/">XLM</a>
 * @since 2020/12/2
 */
public class XlmSite extends BaseResourceSite<SimpleItem> {

    private static final Pattern ITEM_HREF_REGEX = Pattern.compile("/dy/k(?<id>\\d+)\\.html");
    private static final Pattern ITEM_TITLE_REGEX = Pattern.compile("《(?<title>[^《》]*(《[^《》]+》)?[^《》]*)》\\S+\\1\\S+");
    private static final Pattern TYPE_HREF_REGEX = Pattern.compile("/lanmu/xz(?<i>\\d+).html");

    private static final VideoType[] TYPES = new VideoType[]{
            null, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.MOVIE, VideoType.TV,
            VideoType.TV, VideoType.TV, VideoType.TV, VideoType.VARIETY, VideoType.ANIME,
            VideoType.THREE_D, VideoType.TV
    };

    public XlmSite() {
        super("Xlm", "xlmdytt.com");
    }

    @Override
    public List<SimpleItem> findAll() {
        return findAllByPathsIgnoreNotFound(getAllPaths(), this::getItem);
    }

    /**
     * @see <a href="https://www.xlmdytt.com/new.html">Last Update</a>
     */
    private List<String> getAllPaths() {
        Document document;
        try {
            document = getDocument(builder0("/new.html"), false);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Elements tits = document.select(".tit");
        int max = 1;
        for (Element tit : tits) {
            String id = RegexUtils.matchesOrElseThrow(ITEM_HREF_REGEX, tit.attr(ATTR_HREF)).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return getPathsById(max, "/dy/k%d.html", 16962, 30391, 30721);
    }

    private SimpleItem getItem(@Nonnull String path) throws NotFoundException {
        URIBuilder builder = builder0(path);
        Document document = getDocument(builder, true);
        SimpleItem item = new SimpleItem(builder.toString());
        item.setTitle(RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, document.title()).group("title"));

        String typeHref = document.selectFirst("div.conpath").select(TAG_A).get(1).attr(ATTR_HREF);
        String i = RegexUtils.matchesOrElseThrow(TYPE_HREF_REGEX, typeHref).group("i");
        item.setType(Objects.requireNonNull(TYPES[Integer.parseInt(i)]));

        Element downs = document.selectFirst("#downs");
        List<ValidResource> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        for (Element a : downs.select(TAG_A)) {
            String href = a.attr(ATTR_HREF);
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href)) {
                continue;
            }
            try {
                resources.add(ResourceFactory.create(a.text().strip(), href, null, Constants.GBK));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);
        return item;
    }

    @Override
    protected String handleEntity(HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity, Constants.GBK);
    }
}
