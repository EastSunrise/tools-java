package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.entity.item.BdFilmItem;
import wsg.tools.internet.resource.entity.resource.ResourceFactory;
import wsg.tools.internet.resource.entity.resource.base.Resource;
import wsg.tools.internet.resource.entity.resource.base.UnknownResource;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Kingen
 * @see <a href="https://bd-film.cc">BD Film</a>
 * @since 2020/9/23
 */
@Slf4j
public final class BdFilmSite extends BaseResourceSite<BdFilmItem> {

    private static final Pattern ITEM_URL_REGEX =
            Pattern.compile("(https?://www\\.bd-film\\.(cc|com))?(?<path>/(?<type>gy|dh|gq|jd|zx|zy)/(?<id>\\d+).htm)");
    private static final Pattern ITEM_TITLE_REGEX =
            Pattern.compile("(?<title>.*)(迅雷下载)+,百度网盘下载 - BD影视分享 - 最新高清电影资源免费下载");
    private static final Pattern IMDB_INFO_REGEX = Pattern.compile("(title/? ?|((?i)imdb|Db).{0,4})(?<id>tt\\d+)");
    private static final Pattern YEAR_INFO_REGEX = Pattern.compile("(年 ?代|上 ?映|首映|出 ?品|发行).{0,4}(?<year>\\d{4})");
    private static final Pattern VAR_REGEX = Pattern.compile("var urls = \"(?<urls>[0-9A-z+/=]*)\", " +
            "adsUrls = \"[0-9A-z+/=]*\", " +
            "diskUrls = \"(?<disk>[0-9A-z+/=]*)\", " +
            "scoreData = \"(?<imdb>tt\\d+)? ?###(?<db>\\d+)?\"");
    private static final Pattern DISK_RESOURCE_REGEX = Pattern.compile(
            "(\\+链接: )?(?<pwd>[0-9A-z]{4})?" +
                    "(\\|\\|(https?|ttps| https|whttps|\\|https)|\\|?https|\\s+\\|\\|https)" +
                    "://(?<host>www\\.yun\\.cn|pan\\.baidu\\.com)(?<path>/[\\w-./?=&]+)\\s*"
    );

    public BdFilmSite() {
        super("BD-Film", "bd-film.cc");
    }

    @Override
    public Set<BdFilmItem> findAll() {
        return IntStream.range(359, 31256).mapToObj(id -> {
            try {
                return getItem(String.format("/gy/%d.htm", id));
            } catch (NotFoundException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * @param keyword title, id of Douban, or id of IMDb
     */
    @Override
    protected final Set<String> searchItems(@Nonnull String keyword) {
        Document document;
        try {
            document = getDocument(builder0("/search.jspx").addParameter("q", keyword), true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Elements lis = document.selectFirst("ul#content_list").select("li.list-item");
        Set<String> paths = new HashSet<>();
        for (Element li : lis) {
            Element a = li.selectFirst(TAG_A);
            Matcher matcher = ITEM_URL_REGEX.matcher(a.attr(ATTR_HREF));
            if (!matcher.matches()) {
                continue;
            }
            paths.add(matcher.group("path"));
        }
        return paths;
    }

    @Override
    protected final BdFilmItem getItem(@Nonnull String path) throws NotFoundException {
        Document document = getDocument(builder0(path), true);
        BdFilmItem item = new BdFilmItem();

        String location = document.selectFirst("meta[property=og:url]").attr(ATTR_CONTENT);
        if (!ITEM_URL_REGEX.matcher(location).matches()) {
            throw new NotFoundException("Not a film page.");
        }
        item.setUrl(location);
        item.setTitle(RegexUtils.matchesOrElseThrow(ITEM_TITLE_REGEX, document.title()).group("title"));

        Element script = null;
        for (Element element : document.body().select(TAG_SCRIPT)) {
            if (element.html().strip().startsWith("var urls")) {
                script = element;
                break;
            }
        }
        Objects.requireNonNull(script);
        Matcher matcher = RegexUtils.matchesOrElseThrow(VAR_REGEX, script.html().strip().split(";")[0]);
        String db = matcher.group("db");
        item.setDbId(db == null ? null : Long.parseLong(db));
        item.setImdbId(matcher.group("imdb"));

        List<Resource> resources = new LinkedList<>();
        String urls = decode(matcher.group("urls"));
        urls = StringEscapeUtils.unescapeHtml4(urls)
                .replace("<p>", "").replace("</p>", "");
        for (String url : urls.split("#{3,4}\r\n|#{3,4}")) {
            if (StringUtils.isNotBlank(url)) {
                Resource resource = ResourceFactory.create("bd url", url, () -> new UnknownResource(url));
                resources.add(resource);
            }
        }

        String diskStr = decode(matcher.group("disk"));
        if (StringUtils.isNotBlank(diskStr)) {
            String[] diskUrls = diskStr.split("###?\r\n|###|\r\n");
            for (String diskUrl : diskUrls) {
                Matcher m = DISK_RESOURCE_REGEX.matcher(diskUrl);
                if (m.matches()) {
                    String url = "https://" + m.group("host") + m.group("path");
                    resources.add(ResourceFactory.create(m.group("pwd"), url, () -> new UnknownResource(diskUrl)));
                    continue;
                }
                resources.add(new UnknownResource(diskUrl));
            }
        }
        item.setResources(resources);

        Element content = document.selectFirst("dl.content");
        if (content == null) {
            content = document.selectFirst("div.dfg-layout");
        }
        String text = content.text();
        if (item.getImdbId() == null) {
            RegexUtils.ifFind(IMDB_INFO_REGEX, text, m -> item.setImdbId(m.group("id")));
        }
        RegexUtils.ifFind(YEAR_INFO_REGEX, text, m -> item.setYear(Integer.parseInt(m.group("year"))));

        return item;
    }

    private String decode(String urls) {
        urls = new StringBuilder(urls).reverse().toString();
        urls = new String(Base64.getDecoder().decode(urls), Constants.UTF_8);
        return new String(urls.getBytes(StandardCharsets.UTF_16), StandardCharsets.UTF_16);
    }
}
