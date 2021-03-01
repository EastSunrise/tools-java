package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.IntRangeRepositoryImpl;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.base.UnknownResourceException;
import wsg.tools.internet.resource.impl.ResourceFactory;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kingen
 * @see <a href="https://www.bd2020.com/">BD Film</a>
 * @since 2020/9/23
 */
@Slf4j
public final class BdFilmSite extends IntRangeRepositoryImpl<BdFilmItem> {

    private static final int EXCEPT_ID = 30508;
    private static final Pattern ITEM_URL_REGEX = Pattern.compile("https://www\\.bd2020\\.com(?<p>/(?<t>gy|dh|gq|jd|zx|zy)/(?<i>\\d+)\\.htm)");
    private static final Pattern IMDB_INFO_REGEX = Pattern.compile("(title/? ?|((?i)imdb|Db).{0,4})(?<id>tt\\d+)");
    private static final Pattern VAR_REGEX = Pattern.compile("var urls = \"(?<urls>[0-9A-Za-z+/=]*)\", " +
            "adsUrls = \"[0-9A-Za-z+/=]*\", " +
            "diskUrls = \"(?<disk>[0-9A-Za-z+/=]*)\", " +
            "scoreData = \"(?<imdb>tt\\d+)? ?###(?<db>\\d+)?\"");
    private static final Pattern DISK_RESOURCE_REGEX = Pattern.compile(
            "(\\+链接: )?(?<pwd>[0-9A-Za-z]{4})?" +
                    "(\\|\\|(https?|ttps| https|whttps|\\|https)|\\|?https|\\s+\\|\\|https)" +
                    "://(?<host>www\\.yun\\.cn|pan\\.baidu\\.com|pan\\.xunlei\\.com)(?<path>/[\\w-./?=&]+)\\s*"
    );

    private static BdFilmSite instance;

    private BdFilmSite() {
        super("BD-Film", "bd2020.com");
    }

    public static BdFilmSite getInstance() {
        if (instance == null) {
            instance = new BdFilmSite();
        }
        return instance;
    }

    /**
     * @see <a href="https://www.bd2020.com/movies/index.htm">Last Update</a>
     */
    @Override
    protected int max() throws HttpResponseException {
        Document document = getDocument(builder0("/movies/index.htm"), SnapshotStrategy.ALWAYS_UPDATE);
        Elements lis = document.selectFirst("#content_list").select("li.list-item");
        int max = 1;
        for (Element li : lis) {
            Matcher matcher = ITEM_URL_REGEX.matcher(li.selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF));
            if (matcher.matches()) {
                max = Math.max(max, Integer.parseInt(matcher.group("i")));
            }
        }
        return max;
    }

    @Override
    protected BdFilmItem getItem(int id) throws HttpResponseException {
        if (id == EXCEPT_ID) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, "Not a film page");
        }
        Document document = getDocument(builder0("/gy/%d.htm", id), SnapshotStrategy.NEVER_UPDATE);

        Map<String, String> meta = document.select("meta[property]").stream()
                .collect(Collectors.toMap(e -> e.attr("property"), e -> e.attr(CssSelector.ATTR_CONTENT)));
        Elements elements = document.select("meta[name]");
        for (Element element : elements) {
            if (element.hasAttr(CssSelector.ATTR_CONTENT)) {
                meta.put(element.attr(CssSelector.ATTR_NAME), element.attr(CssSelector.ATTR_CONTENT));
            }
        }
        String location = Objects.requireNonNull(meta.get("og:url"));
        if (!ITEM_URL_REGEX.matcher(location).matches()) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, "Not a film page: " + location);
        }
        LocalDateTime updateTime = LocalDateTime.parse(meta.get("og:video:release_date"), Constants.STANDARD_DATE_TIME_FORMATTER);
        BdFilmItem item = new BdFilmItem(id, location, updateTime);

        String[] keywords = document.selectFirst("meta[name=keywords]").attr(CssSelector.ATTR_CONTENT).split(",免费下载");
        item.setTitle(keywords[0].strip());

        Element script = null;
        for (Element element : document.body().select(CssSelector.TAG_SCRIPT)) {
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

        List<AbstractResource> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        String urls = decode(matcher.group("urls"));
        urls = StringEscapeUtils.unescapeHtml4(urls).replace("<p>", "").replace("</p>", "");
        for (String url : urls.split("#{3,4}\r\n|#{3,4}")) {
            if (StringUtils.isNotBlank(url)) {
                try {
                    resources.add(ResourceFactory.create(null, url));
                } catch (InvalidResourceException e) {
                    exceptions.add(e);
                }
            }
        }

        String diskStr = decode(matcher.group("disk"));
        if (StringUtils.isNotBlank(diskStr)) {
            Pair<List<AbstractResource>, List<InvalidResourceException>> pair = parseDiskResources(diskStr);
            resources.addAll(pair.getLeft());
            exceptions.addAll(pair.getRight());
        }
        item.setResources(resources);
        item.setExceptions(exceptions);

        Element content = document.selectFirst("dl.content");
        if (content == null) {
            content = document.selectFirst("div.dfg-layout");
        }
        String text = content.text();
        if (item.getImdbId() == null) {
            Matcher m = IMDB_INFO_REGEX.matcher(text);
            if (m.find()) {
                item.setImdbId(m.group("id"));
            }
        }
        return item;
    }

    private Pair<List<AbstractResource>, List<InvalidResourceException>> parseDiskResources(String diskStr) {
        List<AbstractResource> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        String[] diskUrls = diskStr.split("###?\r\n|###|\r\n");
        for (String diskUrl : diskUrls) {
            Matcher m = DISK_RESOURCE_REGEX.matcher(diskUrl);
            if (m.matches()) {
                String host = m.group("host");
                String url = "https://" + host + m.group("path");
                try {
                    resources.add(ResourceFactory.create(null, url, () -> m.group("pwd")));
                } catch (InvalidResourceException e) {
                    exceptions.add(e);
                }
            } else {
                exceptions.add(new UnknownResourceException("Not a disk resource", null, diskUrl));
            }
        }
        return Pair.of(resources, exceptions);
    }

    private String decode(String urls) {
        urls = new StringBuilder(urls).reverse().toString();
        urls = new String(Base64.getDecoder().decode(urls), Constants.UTF_8);
        return new String(urls.getBytes(StandardCharsets.UTF_16), StandardCharsets.UTF_16);
    }
}
