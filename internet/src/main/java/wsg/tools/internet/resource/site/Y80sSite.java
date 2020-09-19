package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.base.SchemeEnum;
import wsg.tools.internet.resource.download.Downloader;
import wsg.tools.internet.resource.entity.AbstractResource;
import wsg.tools.internet.resource.entity.IdentifiedTitleDetail;
import wsg.tools.internet.resource.entity.SimpleTitle;
import wsg.tools.internet.resource.entity.VideoTypeEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="http://m.y80s.com">80s</a>
 * @since 2020/9/9
 */
@Slf4j
public class Y80sSite extends AbstractVideoResourceSite<IdentifiedTitleDetail> {

    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com(/(ju|movie|dm|trailer|mv|video|course|zy)/\\d+)");
    private static final Pattern DOUBAN_REVIEWS_REGEX = Pattern.compile("//movie.douban.com/subject/(\\d*) ?//?reviews");
    private static final String UNKNOWN_YEAR = "未知";

    public Y80sSite() {
        super("80s", SchemeEnum.HTTP, "m.y80s.com", 0.1);
    }

    /**
     * Search resources of movies by the given title and id of Douban.
     */
    @Override
    public List<AbstractResource> collectMovie(@Nonnull String title, @Nonnull Year year, @Nullable Long dbId) throws IOException {
        List<AbstractResource> resources = new ArrayList<>();
        for (SimpleTitle item : search(title)) {
            // excluded: not same type
            if (!Objects.equals(item.getType(), VideoTypeEnum.MOVIE)) {
                log.info("Excluded title: {}, required: {}, provided: {}.", item.getTitle(), VideoTypeEnum.MOVIE, item.getType());
                continue;
            }
            if (!Objects.equals(item.getYear(), year)) {
                log.info("Excluded title: {}, required: {}, provided: {}.", item.getTitle(), year, item.getYear());
                continue;
            }
            IdentifiedTitleDetail resource = find(item);
            // excluded: not same douban id if provided
            if (dbId != null) {
                if (!Objects.equals(resource.getDbId(), dbId)) {
                    log.info("Excluded title: {}, required: {}, provided: {}.", item.getTitle(), dbId, resource.getDbId());
                    continue;
                }
            }
            // excluded: not a possible title
            if (notPossibleTitles(title, year.getValue(), item.getTitle())) {
                log.info("Excluded title: {}, not a possible title by {}.", item.getTitle(), title);
                continue;
            }
            log.info("Chosen title: {}", item.getTitle());
            resources.addAll(resource.getResources());
        }
        return resources;
    }

    @Override
    public List<SimpleTitle> search(@Nonnull String keyword) throws IOException {
        List<BasicNameValuePair> params = Collections.singletonList(new BasicNameValuePair("keyword", keyword));
        Elements as = postDocument(uriBuilder("/search"), params, true).select("a.list-group-item");
        List<SimpleTitle> titles = new ArrayList<>();
        for (Element a : as) {
            SimpleTitle title = new SimpleTitle();
            Matcher matcher = AssertUtils.matches(TITLE_HREF_REGEX, a.attr(ATTR_HREF));
            title.setPath(matcher.group(1));
            title.setType(EnumUtilExt.deserializeAka(matcher.group(2), VideoTypeEnum.class));
            Element small = a.selectFirst(TAG_SMALL);
            title.setTitle(small.previousSibling().outerHtml().strip());
            String text = small.text();
            title.setYear((StringUtils.isBlank(text) || UNKNOWN_YEAR.equals(text)) ? null : Year.parse(text));
            titles.add(title);
        }
        return titles;
    }

    @Override
    public IdentifiedTitleDetail find(@Nonnull SimpleTitle title) throws IOException {
        IdentifiedTitleDetail detail = new IdentifiedTitleDetail();
        Document document = getDocument(uriBuilder(title.getPath()), true);
        String idStr = AssertUtils.find(DOUBAN_REVIEWS_REGEX, document.selectFirst("p.col-xs-6").html()).group(1);
        detail.setDbId("".equals(idStr) ? null : Long.parseLong(idStr));
        List<AbstractResource> resources = new ArrayList<>();
        for (Element tr : document.select(TAG_TR)) {
            Element a = tr.selectFirst(TAG_A);
            AbstractResource resource = Downloader.classifyUrl(a.attr(ATTR_HREF));
            resource.setTitle(a.text().strip());
            resources.add(resource);
        }
        detail.setResources(resources);
        return detail;
    }
}
