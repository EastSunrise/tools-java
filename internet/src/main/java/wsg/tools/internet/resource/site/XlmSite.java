package wsg.tools.internet.resource.site;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.entity.item.impl.SimpleItem;
import wsg.tools.internet.resource.entity.resource.ResourceFactory;
import wsg.tools.internet.resource.entity.resource.base.Resource;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://xlmdytt.com">XLM</a>
 * @since 2020/12/2
 */
public class XlmSite extends BaseResourceSite<SimpleItem> {

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
    protected List<String> getAllPaths() {
        return getPathsById(1, 31588, id -> String.format("/dy/k%d.html", id), 16962, 30391, 30721);
    }

    @Override
    protected Set<String> searchItems(@Nonnull String keyword) {
        return null;
    }

    @Override
    protected SimpleItem getItem(@Nonnull String path) throws NotFoundException {
        URIBuilder builder = builder0(path);
        Document document = getDocument(builder, true);
        SimpleItem item = new SimpleItem(builder.toString());
        item.setTitle(RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, document.title()).group("title"));

        String typeHref = document.selectFirst("div.conpath").select(TAG_A).get(1).attr(ATTR_HREF);
        String i = RegexUtils.matchesOrElseThrow(TYPE_HREF_REGEX, typeHref).group("i");
        item.setType(Objects.requireNonNull(TYPES[Integer.parseInt(i)]));

        Element downs = document.selectFirst("#downs");
        List<Resource> resources = new LinkedList<>();
        for (Element a : downs.select(TAG_A)) {
            String href = a.attr(ATTR_HREF);
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href)) {
                continue;
            }
            resources.add(ResourceFactory.create(a.text().strip(), href, Constants.GBK));
        }
        item.setResources(resources);

        return item;
    }

    @Override
    protected String handleEntity(HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity, Constants.GBK);
    }
}
