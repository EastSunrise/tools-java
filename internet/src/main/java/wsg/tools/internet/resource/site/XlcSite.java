package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.resource.download.Downloader;
import wsg.tools.internet.resource.entity.AbstractResource;
import wsg.tools.internet.resource.entity.SimpleTitle;
import wsg.tools.internet.resource.entity.TitleDetail;
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
 * @see <a href="https://aixiaoju.com">AXJ</a>
 * @since 2020/9/9
 */
@Slf4j
public class XlcSite extends AbstractVideoResourceSite<TitleDetail> {

    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("(https://www\\.(xunleicang\\.in|xlc2020\\.com))?(/vod-read-id-\\d+.html)");
    private static final int TYPE_INFO_START = "类型: ".length();

    public XlcSite() {
        super("XLC", "www.xunleicang.in", 0.1);
    }

    /**
     * Search resources of movies by the given title and id of Douban.
     */
    @Override
    public List<AbstractResource> collectMovie(@Nonnull String title, @Nonnull Year year, @Nullable Long dbId) throws IOException {
        List<AbstractResource> resources = new ArrayList<>();
        for (SimpleTitle item : search(title)) {
            if (!Objects.equals(item.getType(), VideoTypeEnum.MOVIE)) {
                log.info("Excluded title: {}, required: {}, provided: {}.", item.getTitle(), VideoTypeEnum.MOVIE, item.getType());
                continue;
            }
            if (!Objects.equals(item.getYear(), year)) {
                log.info("Excluded title: {}, required: {}, provided: {}.", item.getTitle(), year, item.getYear());
                continue;
            }
            if (notPossibleTitles(title, year.getValue(), item.getTitle())) {
                log.info("Excluded title: {}, not a possible title by {}.", item.getTitle(), title);
                continue;
            }
            resources.addAll(find(item).getResources());
            log.info("Chosen title: {}", item.getTitle());
        }
        return resources;
    }

    @Override
    public List<SimpleTitle> search(@Nonnull String keyword) throws IOException {
        List<BasicNameValuePair> params = Collections.singletonList(new BasicNameValuePair("wd", keyword));
        Document document = postDocument(uriBuilder("/vod-search"), params);
        List<SimpleTitle> titles = new ArrayList<>();
        String movList = "div.movList4";
        for (Element div : document.select(movList)) {
            Element h3 = div.selectFirst(TAG_H3);
            Element a = h3.selectFirst(TAG_A);
            String typeInfo = div.selectFirst("li.playactor").nextElementSibling().text();
            int year = Integer.parseInt(a.nextElementSibling().text());
            Matcher matcher = TITLE_HREF_REGEX.matcher(a.attr(ATTR_HREF));
            if (!matcher.matches()) {
                continue;
            }
            SimpleTitle title = new SimpleTitle();
            title.setPath(matcher.group(3));
            title.setType(EnumUtilExt.deserializeAka(typeInfo.substring(TYPE_INFO_START), VideoTypeEnum.class));
            title.setTitle(a.text().strip());
            title.setYear(year == 0 ? null : Year.of(year));
            titles.add(title);
        }
        return titles;
    }

    @Override
    public TitleDetail find(@Nonnull SimpleTitle title) throws IOException {
        TitleDetail detail = new TitleDetail();
        List<AbstractResource> resources = new ArrayList<>();
        String downList = "ul.down-list";
        String item = "li.item";
        for (Element ul : getDocument(uriBuilder(title.getPath()), true).select(downList)) {
            for (Element li : ul.select(item)) {
                Element a = li.selectFirst(TAG_A);
                String href = a.attr(ATTR_HREF);
                AbstractResource resource = Downloader.classifyUrl(href);
                resource.setTitle(a.text().strip());
                resources.add(resource);
            }
        }
        detail.setResources(resources);
        return detail;
    }
}
