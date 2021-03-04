package wsg.tools.internet.resource.movie;

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
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.IterableRepositoryImpl;
import wsg.tools.internet.base.impl.WithoutNextDocument;
import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.LinkFactory;
import wsg.tools.internet.download.UnknownResourceException;
import wsg.tools.internet.download.base.AbstractLink;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kingen
 * @see <a href="https://www.bd2020.com/">BD Movies</a>
 * @since 2020/9/23
 */
@Slf4j
public final class BdMovieSite extends BaseSite {

    private static final Pattern ITEM_URL_REGEX = Pattern.compile("https://www\\.bd2020\\.com(?<p>/(?<t>gy|dh|gq|jd|zx|zy)/(?<id>\\d+)\\.htm)");
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

    private static BdMovieSite instance;

    private BdMovieSite() {
        super("BD-Movie", new BasicHttpSession("bd2020.com"));
    }

    public synchronized static BdMovieSite getInstance() {
        if (instance == null) {
            instance = new BdMovieSite();
        }
        return instance;
    }

    public IterableRepository<BdMovieItem> getRepository(BdMovieType type) {
        return new IterableRepositoryImpl<>(id -> findItem(type, id), type.first());
    }

    /**
     * Obtains an item by the given type and id.
     */
    public BdMovieItem findItem(@Nonnull BdMovieType type, @Nonnull Integer id) throws HttpResponseException {
        Document document = getDocument(builder0("/%s/%d.htm", type.getText(), id), new WithoutNextDocument<>(this::getNext));
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
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, "Not a movie page: " + location);
        }
        LocalDateTime updateTime = LocalDateTime.parse(meta.get("og:video:release_date"), Constants.STANDARD_DATE_TIME_FORMATTER);
        BdMovieItem item = new BdMovieItem(id, location, updateTime);

        item.setNext(getNext(document));
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

        List<AbstractLink> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        String urls = decode(matcher.group("urls"));
        urls = StringEscapeUtils.unescapeHtml4(urls).replace("<p>", "").replace("</p>", "");
        for (String url : urls.split("#{3,4}\r\n|#{3,4}")) {
            if (StringUtils.isNotBlank(url)) {
                try {
                    resources.add(LinkFactory.create(null, url));
                } catch (InvalidResourceException e) {
                    exceptions.add(e);
                }
            }
        }

        String diskStr = decode(matcher.group("disk"));
        if (StringUtils.isNotBlank(diskStr)) {
            Pair<List<AbstractLink>, List<InvalidResourceException>> pair = parseDiskResources(diskStr);
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

    private Integer getNext(Document document) {
        Elements children = document.selectFirst("div-neighbour").children();
        AssertUtils.requireEquals(children.size(), 2);
        Element next = children.get(0).selectFirst(CssSelector.TAG_A);
        if (next == null) {
            return null;
        }
        return Integer.parseInt(RegexUtils.matchesOrElseThrow(ITEM_URL_REGEX, next.attr(CssSelector.ATTR_HREF)).group("id"));
    }

    private Pair<List<AbstractLink>, List<InvalidResourceException>> parseDiskResources(String diskStr) {
        List<AbstractLink> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        String[] diskUrls = diskStr.split("###?\r\n|###|\r\n");
        for (String diskUrl : diskUrls) {
            Matcher m = DISK_RESOURCE_REGEX.matcher(diskUrl);
            if (m.matches()) {
                String host = m.group("host");
                String url = "https://" + host + m.group("path");
                try {
                    resources.add(LinkFactory.create(null, url, () -> m.group("pwd")));
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
