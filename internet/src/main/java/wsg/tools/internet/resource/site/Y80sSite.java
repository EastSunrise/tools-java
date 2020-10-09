package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.base.NotFoundException;
import wsg.tools.internet.base.SchemeEnum;
import wsg.tools.internet.resource.common.ResourceUtil;
import wsg.tools.internet.resource.common.VideoTypeEnum;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.title.IdentifiedDetail;
import wsg.tools.internet.resource.entity.title.SimpleTitle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="http://m.y80s.com">80s</a>
 * @since 2020/9/9
 */
@Slf4j
public class Y80sSite extends BaseResourceSite<SimpleTitle, IdentifiedDetail> {

    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com(/(ju|movie|dm|trailer|mv|video|course|zy)/\\d+)");
    private static final Pattern DOUBAN_REVIEWS_REGEX = Pattern.compile("//movie.douban.com/subject/(\\d*) ?//?reviews");
    private static final String UNKNOWN_YEAR = "未知";

    public Y80sSite() {
        super("80s", SchemeEnum.HTTP, "m.y80s.com", 0.1);
    }

    /**
     * Search and collect resources based on the given arguments.
     */
    public Set<AbstractResource> collectMovie(String title, int year, @Nullable Long dbId) {
        Set<AbstractResource> resources = new HashSet<>();
        for (SimpleTitle item : search(title)) {
            String itemTitle = item.getTitle();
            IdentifiedDetail resource = find(item);
            Long providedId = resource.getDbId();
            if (dbId == null || providedId == null) {
                if (!validate(itemTitle, item.getType(), VideoTypeEnum.MOVIE)) {
                    continue;
                }
                if (!validate(itemTitle, item.getYear(), year)) {
                    continue;
                }
                if (notPossibleTitle(itemTitle, title, year)) {
                    continue;
                }
            } else if (!validate(itemTitle, providedId, dbId)) {
                continue;
            }
            log.info("Chosen title: {}", itemTitle);
            resources.addAll(resource.getResources());
        }
        return resources;
    }

    @Override
    protected final Set<SimpleTitle> search(@Nonnull String keyword) {
        List<BasicNameValuePair> params = Collections.singletonList(new BasicNameValuePair("keyword", keyword));
        Elements as;
        try {
            as = postDocument(builder0("/search"), params, true).select("a.list-group-item");
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Set<SimpleTitle> titles = new HashSet<>();
        for (Element a : as) {
            SimpleTitle title = new SimpleTitle();
            Matcher matcher = AssertUtils.matches(TITLE_HREF_REGEX, a.attr(ATTR_HREF));
            title.setPath(matcher.group(1));
            title.setType(EnumUtilExt.deserializeAka(matcher.group(2), VideoTypeEnum.class));
            Element small = a.selectFirst(TAG_SMALL);
            title.setTitle(small.previousSibling().outerHtml().strip());
            String text = small.text();
            title.setYear((StringUtils.isBlank(text) || UNKNOWN_YEAR.equals(text)) ? null : Integer.parseInt(text));
            titles.add(title);
        }
        return titles;
    }

    @Override
    protected final IdentifiedDetail find(@Nonnull SimpleTitle title) {
        IdentifiedDetail detail = new IdentifiedDetail();
        Document document;
        try {
            document = getDocument(builder0(title.getPath()), true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        String idStr = AssertUtils.find(DOUBAN_REVIEWS_REGEX, document.selectFirst("p.col-xs-6").html()).group(1);
        detail.setDbId("".equals(idStr) ? null : Long.parseLong(idStr));
        Set<AbstractResource> resources = new HashSet<>();
        for (Element tr : document.select(TAG_TR)) {
            Element a = tr.selectFirst(TAG_A);
            AbstractResource resource = ResourceUtil.classifyUrl(a.attr(ATTR_HREF));
            resource.setTitle(a.text().strip());
            resources.add(resource);
        }
        detail.setResources(resources);
        return detail;
    }
}
