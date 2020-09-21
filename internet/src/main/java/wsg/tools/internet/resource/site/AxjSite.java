package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.base.ContentTypeEnum;
import wsg.tools.internet.resource.download.Downloader;
import wsg.tools.internet.resource.entity.AbstractResource;
import wsg.tools.internet.resource.entity.SimpleTitle;
import wsg.tools.internet.resource.entity.TitleDetail;
import wsg.tools.internet.resource.entity.VideoTypeEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kingen
 * @see <a href="https://aixiaoju.com">AXJ</a>
 * @since 2020/9/10
 */
@Slf4j
public class AxjSite extends AbstractVideoResourceSite<TitleDetail> {

    private static final String ERROR_TITLE = "错误提示 - 爱笑聚";
    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("/read-\\d+");
    private static final Pattern YEAR_INFO_REGEX = Pattern.compile("[(（]\\s?(\\d{4})([~-](\\d{4}))?[)）]");

    public AxjSite() {
        super("Axj", "aixiaoju.com", 0.06);
    }

    @Override
    public List<AbstractResource> collectMovie(@Nonnull String title, @Nonnull Year year, @Nullable Long dbId) {
        return null;
    }

    @Override
    public List<SimpleTitle> search(@Nonnull String keyword) throws IOException {
        return get(builder0("/app-thread-run")
                .addParameter("app", "search")
                .addParameter("keywords", keyword)
                .addParameter("orderby", "lastpost_time"))
                .selectFirst("div.search_content")
                .select(TAG_DL).stream()
                .map(dl -> {
                    Element a = dl.selectFirst(TAG_A);
                    Element forum = dl.selectFirst("div.info").select(TAG_A).get(1);
                    String title = a.text();
                    String text = dl.selectFirst("div.text").text();
                    Year year = null, endYear = null;
                    if (text.startsWith(title)) {
                        text = text.substring(title.length());
                        Matcher matcher = YEAR_INFO_REGEX.matcher(text);
                        if (matcher.find()) {
                            year = Year.parse(matcher.group(1));
                            String endStr = matcher.group(3);
                            if (endStr != null) {
                                endYear = Year.parse(endStr);
                            }
                        }
                    }
                    SimpleTitle simpleTitle = new SimpleTitle();
                    simpleTitle.setPath(AssertUtils.matches(TITLE_HREF_REGEX, a.attr(ATTR_HREF)).group());
                    simpleTitle.setTitle(title);
                    simpleTitle.setYear(year);
                    simpleTitle.setType(EnumUtilExt.deserializeAka(forum.attr(ATTR_HREF), VideoTypeEnum.class));
                    simpleTitle.setEndYear(endYear);
                    return simpleTitle;
                })
                .collect(Collectors.toList());
    }

    @Override
    public TitleDetail find(@Nonnull SimpleTitle title) throws IOException {
        TitleDetail detail = new TitleDetail();
        detail.setResources(get(builder0(title.getPath()))
                .selectFirst("div.editor_content").select(TAG_A)
                .stream().map(a -> {
                    AbstractResource resource = Downloader.classifyUrl(a.attr(ATTR_HREF));
                    resource.setTitle(a.text());
                    return resource;
                })
                .collect(Collectors.toList())
        );
        return detail;
    }

    private Document get(URIBuilder builder) throws IOException {
        while (true) {
            try {
                return super.getDocument(builder, true);
            } catch (HttpResponseException e) {
                if (e.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    continue;
                }
                throw e;
            }
        }
    }

    @Override
    public String handleResponse(String content, ContentTypeEnum contentType) throws HttpResponseException {
        if (!ContentTypeEnum.HTML.equals(contentType)) {
            return content;
        }
        Document document = Jsoup.parse(content);
        if (ERROR_TITLE.equals(document.title())) {
            throw new HttpResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR, document.selectFirst("li#J_html_error").text());
        }
        return content;
    }
}
