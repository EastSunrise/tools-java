package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.base.NotFoundException;
import wsg.tools.internet.resource.common.ResourceUtil;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.resource.PanResource;
import wsg.tools.internet.resource.entity.title.BaseTitle;
import wsg.tools.internet.resource.entity.title.IdentifiedDetail;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kingen
 * @see <a href="https://bd-film.cc">BD Film</a>
 * @since 2020/9/23
 */
@Slf4j
public final class BdFilmSite extends BaseResourceSite<BaseTitle, IdentifiedDetail> {

    private static final Pattern TITLE_HREF_REGEX =
            Pattern.compile("https?://www\\.bd-film\\.(cc|com)(?<path>/(?<type>[a-z]{2}|hjtj|zuitop)/(?<id>\\d+).htm)");
    private static final Pattern URLS_REGEX = Pattern.compile("(var urls|adsUrls|diskUrls) = \"(?<urls>[0-9A-z+/=]*)\"");
    private static final Pattern SCORE_REGEX = Pattern.compile("scoreData = \"(?<imdb>tt\\d+)? ?###(?<db>\\d+)?\"");
    private static final Pattern IMDB_URL_REGEX = Pattern.compile("http://www\\.imdb\\.com/title/(?<id>tt\\d+)/?");
    private static final String UC_YUN_HOST = "yun.cn";

    public BdFilmSite() {
        super("BD-Film", "bd-film.cc");
    }

    public Set<AbstractResource> collectMovie(String title, String imdbId, Long dbId) {
        if (imdbId == null && dbId == null) {
            throw new IllegalArgumentException("At least one of the ids is provided.");
        }
        Set<BaseTitle> titles = new HashSet<>();
        if (title != null) {
            titles.addAll(search(title));
        }
        if (imdbId != null) {
            titles.addAll(search(imdbId));
        }
        if (dbId != null) {
            titles.addAll(search(String.valueOf(dbId)));
        }
        Set<AbstractResource> resources = new HashSet<>();
        for (BaseTitle baseTitle : titles) {
            IdentifiedDetail detail = find(baseTitle);
            if (imdbId != null && !validate(baseTitle.getTitle(), detail.getImdbId(), imdbId)) {
                continue;
            }
            if (dbId != null && !validate(baseTitle.getTitle(), detail.getDbId(), dbId)) {
                continue;
            }
            log.info("Chosen title: {}", baseTitle.getTitle());
            resources.addAll(detail.getResources());
        }
        return resources;
    }

    @Override
    protected final Set<BaseTitle> search(@Nonnull String keyword) {
        Document document;
        try {
            document = getDocument(builder0("/search.jspx").addParameter("q", keyword), true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Elements lis = document.selectFirst("ul#content_list").select("li.list-item");
        Set<BaseTitle> titles = new HashSet<>();
        for (Element li : lis) {
            Element a = li.selectFirst(TAG_A);
            Matcher matcher = AssertUtils.matches(TITLE_HREF_REGEX, a.attr(ATTR_HREF));
            String type = matcher.group("type");
            if ("hjtj".equals(type) || "zuitop".equals(type)) {
                continue;
            }
            BaseTitle title = new BaseTitle();
            title.setTitle(a.attr(ATTR_TITLE));
            title.setPath(matcher.group("path"));
            titles.add(title);
        }
        return titles;
    }

    @Override
    protected final IdentifiedDetail find(@Nonnull BaseTitle title) {
        IdentifiedDetail detail = new IdentifiedDetail();
        Document document;
        try {
            document = getDocument(builder0(title.getPath()), true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }

        Element script = document.body().selectFirst("> script[type=text/javascript]");
        if (script == null) {
            detail.setResources(new HashSet<>());
            return detail;
        }
        String[] parts = script.html().strip().split(";");
        String[] data = parts[0].split(",");
        String urlsStr = decode(AssertUtils.matches(URLS_REGEX, data[0].strip()).group("urls"));
        urlsStr = StringEscapeUtils.unescapeHtml4(urlsStr)
                .replace("<p>", "")
                .replace("</p>", "");
        String[] urls = StringUtils.split(urlsStr, "#\r\n");
        Set<AbstractResource> resources = Arrays.stream(urls)
                .map(ResourceUtil::classifyUrl).collect(Collectors.toSet());

        String diskStr = decode(AssertUtils.matches(URLS_REGEX, data[2].strip()).group("urls"));
        if (StringUtils.isNotBlank(diskStr)) {
            String[] diskUrls = diskStr.split("###");
            Arrays.stream(diskUrls)
                    .forEach(url -> {
                        if (!url.contains(UC_YUN_HOST)) {
                            String[] strings = StringUtils.splitByWholeSeparator(url, "||");
                            if (strings.length == 1) {
                                resources.add(new PanResource(url));
                            } else {
                                resources.add(new PanResource(strings[1].strip(), strings[0].strip()));
                            }
                        }
                    });
        }
        detail.setResources(resources);

        Matcher matcher = AssertUtils.matches(SCORE_REGEX, data[3].strip());
        String db = matcher.group("db");
        if (db != null) {
            detail.setDbId(Long.parseLong(db));
        }
        detail.setImdbId(matcher.group("imdb"));

        if (detail.getImdbId() == null) {
            Element content = document.selectFirst("dl.content");
            if (content != null) {
                Matcher m = IMDB_URL_REGEX.matcher(content.text());
                if (m.find()) {
                    detail.setImdbId(m.group("id"));
                }
            }
        }

        return detail;
    }

    private String decode(String urls) {
        urls = new StringBuilder(urls).reverse().toString();
        urls = new String(Base64.getDecoder().decode(urls), Constants.UTF_8);
        return new String(urls.getBytes(StandardCharsets.UTF_16), StandardCharsets.UTF_16);
    }
}
