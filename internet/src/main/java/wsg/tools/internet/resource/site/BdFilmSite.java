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
import wsg.tools.internet.base.SiteStatus;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.entity.item.impl.BdFilmItem;
import wsg.tools.internet.resource.entity.resource.ResourceFactory;
import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.UnknownResourceException;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://www.bd-film.cc/">BD Film</a>
 * @since 2020/9/23
 */
@Slf4j
@SiteStatus(status = SiteStatus.Status.RESTRICTED)
public final class BdFilmSite extends BaseResourceSite<BdFilmItem> {

    private static final Pattern ITEM_URL_REGEX = Pattern.compile("(https?://www\\.bd-film\\.(cc|com))?(?<path>/(?<type>gy|dh|gq|jd|zx|zy)/(?<id>\\d+)\\.htm)");
    private static final Pattern IMDB_INFO_REGEX = Pattern.compile("(title/? ?|((?i)imdb|Db).{0,4})(?<id>tt\\d+)");
    private static final Pattern VAR_REGEX = Pattern.compile("var urls = \"(?<urls>[0-9A-Za-z+/=]*)\", " +
            "adsUrls = \"[0-9A-Za-z+/=]*\", " +
            "diskUrls = \"(?<disk>[0-9A-Za-z+/=]*)\", " +
            "scoreData = \"(?<imdb>tt\\d+)? ?###(?<db>\\d+)?\"");
    private static final Pattern DISK_RESOURCE_REGEX = Pattern.compile(
            "(\\+链接: )?(?<pwd>[0-9A-Za-z]{4})?" +
                    "(\\|\\|(https?|ttps| https|whttps|\\|https)|\\|?https|\\s+\\|\\|https)" +
                    "://(?<host>www\\.yun\\.cn|pan\\.baidu\\.com)(?<path>/[\\w-./?=&]+)\\s*"
    );

    public BdFilmSite() {
        super("BD-Film", "bd-film.cc");
    }

    @Override
    public List<BdFilmItem> findAll() {
        return findAllByPathsIgnoreNotFound(getAllPaths(), this::getItem);
    }

    /**
     * @see <a href="https://www.bd-film.cc/movies/index.htm">Last Update</a>
     */
    private List<String> getAllPaths() {
        Document document;
        try {
            document = getDocument(builder0("/movies/index.htm"), false);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Elements lis = document.selectFirst("#content_list").select("li.list-item");
        int max = 1;
        for (Element li : lis) {
            Matcher matcher = ITEM_URL_REGEX.matcher(li.selectFirst(TAG_A).attr(ATTR_HREF));
            if (matcher.matches()) {
                max = Math.max(max, Integer.parseInt(matcher.group("id")));
            }
        }
        return getPathsById(max, "/gy/%d.htm", 30508);
    }

    private BdFilmItem getItem(@Nonnull String path) throws NotFoundException {
        Document document = getDocument(builder0(path), true);

        String location = document.selectFirst("meta[property=og:url]").attr(ATTR_CONTENT);
        if (!ITEM_URL_REGEX.matcher(location).matches()) {
            throw new NotFoundException("Not a film page: " + location);
        }
        BdFilmItem item = new BdFilmItem(location);
        String[] keywords = document.selectFirst("meta[name=keywords]").attr(ATTR_CONTENT).split(",免费下载");
        item.setTitle(keywords[0].strip());

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

        List<ValidResource> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        String urls = decode(matcher.group("urls"));
        urls = StringEscapeUtils.unescapeHtml4(urls).replace("<p>", "").replace("</p>", "");
        for (String url : urls.split("#{3,4}\r\n|#{3,4}")) {
            if (StringUtils.isNotBlank(url)) {
                try {
                    resources.add(ResourceFactory.create(null, url, null));
                } catch (InvalidResourceException e) {
                    exceptions.add(e);
                }
            }
        }

        String diskStr = decode(matcher.group("disk"));
        if (StringUtils.isNotBlank(diskStr)) {
            String[] diskUrls = diskStr.split("###?\r\n|###|\r\n");
            for (String diskUrl : diskUrls) {
                Matcher m = DISK_RESOURCE_REGEX.matcher(diskUrl);
                if (m.matches()) {
                    String url = "https://" + m.group("host") + m.group("path");
                    try {
                        resources.add(ResourceFactory.create(null, url, m.group("pwd")));
                    } catch (InvalidResourceException e) {
                        exceptions.add(e);
                    }
                    continue;
                }
                exceptions.add(new UnknownResourceException("Not a disk resource", null, diskUrl));
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);

        Element content = document.selectFirst("dl.content");
        if (content == null) {
            content = document.selectFirst("div.dfg-layout");
        }
        String text = content.text();
        if (item.getImdbId() == null) {
            RegexUtils.ifFind(IMDB_INFO_REGEX, text, m -> item.setImdbId(m.group("id")));
        }
        return item;
    }

    private String decode(String urls) {
        urls = new StringBuilder(urls).reverse().toString();
        urls = new String(Base64.getDecoder().decode(urls), Constants.UTF_8);
        return new String(urls.getBytes(StandardCharsets.UTF_16), StandardCharsets.UTF_16);
    }
}
