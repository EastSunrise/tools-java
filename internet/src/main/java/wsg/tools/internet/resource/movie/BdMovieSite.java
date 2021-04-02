package wsg.tools.internet.resource.movie;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.RequestBuilder;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.LinkFactory;
import wsg.tools.internet.download.UnknownResourceException;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.impl.HttpLink;

/**
 * @author Kingen
 * @see <a href="https://www.bd2020.com/">BD Movies</a>
 * @since 2020/9/23
 */
@ConcreteSite
@Slf4j
public final class BdMovieSite extends AbstractListResourceSite<BdMovieItem> {

    private static final int MIN_ID = 348;
    private static final Range<Integer> EXCEPTS = Range.between(30508, 30508);

    public BdMovieSite() {
        super("BD-Movie", new BasicHttpSession("bd2020.com"));
    }

    /**
     * Constructs the repository of all items from 1 to {@link #latest()}. <strong>Almost 10% of the
     * items are not found</strong>.
     */
    @Override
    @Nonnull
    public ListRepository<Integer, BdMovieItem> getRepository() throws OtherResponseException {
        IntStream stream = IntStream.rangeClosed(MIN_ID, latest())
            .filter(id -> !EXCEPTS.contains(id));
        return Repositories.list(this, stream.boxed().collect(Collectors.toList()));
    }

    /**
     * Returns the identifier of the latest item.
     *
     * @see <a href="https://www.bd2020.com/movies/index.htm">Last Update</a>
     */
    public int latest() throws OtherResponseException {
        Document document = findDocument(builder0("/movies/index.htm"),
            t -> true);
        Elements lis = document.selectFirst("#content_list").select(".list-item");
        int latest = 1;
        for (Element li : lis) {
            String href = li.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
            Matcher matcher = Lazy.ITEM_URL_REGEX.matcher(href);
            if (matcher.matches()) {
                latest = Math.max(latest, Integer.parseInt(matcher.group("id")));
            }
        }
        return latest;
    }

    @Nonnull
    @Override
    public BdMovieItem findById(@Nonnull Integer id)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = builder0("/zx/%d.htm", id);
        Document document = getDocument(builder, doc -> getNext(doc) == null);
        Map<String, String> metadata = DocumentUtils.getMetadata(document);
        String location = Objects.requireNonNull(metadata.get("og:url"));
        Matcher urlMatcher = Lazy.ITEM_URL_REGEX.matcher(location);
        if (!urlMatcher.matches()) {
            throw new NotFoundException("Not a movie page");
        }
        String realTypeText = urlMatcher.group("t");
        BdMovieType realType = EnumUtilExt.valueOfText(BdMovieType.class, realTypeText, false);
        String release = metadata.get("og:video:release_date");
        LocalDateTime updateTime = LocalDateTime.parse(release, Constants.YYYY_MM_DD_HH_MM_SS);
        BdMovieItem item = new BdMovieItem(id, location, realType, updateTime);
        item.setNext(getNext(document));
        item.setTitle(metadata.get("og:title"));
        String cover = metadata.get("og:image");
        if (!cover.isEmpty()) {
            if (cover.startsWith(Constants.URL_PATH_SEPARATOR)) {
                cover = builder0(cover).toString();
            }
            item.setCover(NetUtils.createURL(cover));
        }

        String varUrls = getVarUrls(document);
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.VAR_REGEX, varUrls);
        String db = matcher.group("db");
        item.setDbId(db == null ? null : Long.parseLong(db));
        item.setImdbId(matcher.group("imdb"));

        List<AbstractLink> links = new ArrayList<>();
        List<InvalidResourceException> exceptions = new ArrayList<>();
        String urls = decode(matcher.group("urls"));
        urls = StringEscapeUtils.unescapeHtml4(urls).replace("<p>", "").replace("</p>", "");
        for (String url : Lazy.URLS_SEPARATOR.split(urls)) {
            if (StringUtils.isNotBlank(url)) {
                try {
                    links.add(LinkFactory.create(null, url));
                } catch (InvalidResourceException e) {
                    exceptions.add(e);
                }
            }
        }

        String diskStr = decode(matcher.group("disk"));
        if (StringUtils.isNotBlank(diskStr)) {
            getDiskResources(diskStr, links, exceptions);
        }
        item.setLinks(links);
        item.setExceptions(exceptions);

        Element content = document.selectFirst("dl.content");
        if (content == null) {
            content = document.selectFirst("div.dfg-layout");
        }
        String text = content.text();
        if (item.getImdbId() == null) {
            Matcher matcher1 = Lazy.IMDB_INFO_REGEX.matcher(text);
            if (matcher1.find()) {
                item.setImdbId(matcher1.group("id"));
            }
        }
        return item;
    }

    private String getVarUrls(Document document) {
        for (Element script : document.body().select(CssSelectors.TAG_SCRIPT)) {
            String html = script.html().strip();
            if (html.startsWith("var urls")) {
                return html.split(";")[0];
            }
        }
        throw new NoSuchElementException("Can't get var urls");
    }

    private Integer getNext(Document document) {
        Element div = document.selectFirst(".dfg-neighbour");
        if (div == null) {
            return null;
        }
        Elements children = div.children();
        AssertUtils.requireEquals(children.size(), 2);
        Element next = children.get(0).selectFirst(CssSelectors.TAG_A);
        if (next == null) {
            return null;
        }
        String href = next.attr(CssSelectors.ATTR_HREF);
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.ITEM_URL_REGEX, href);
        return Integer.parseInt(matcher.group("id"));
    }

    private void getDiskResources(String diskStr, List<AbstractLink> links,
        List<InvalidResourceException> exceptions) {
        String[] diskUrls = Lazy.DISK_URLS_SEPARATOR.split(diskStr);
        for (String diskUrl : diskUrls) {
            Matcher matcher = Lazy.DISK_RESOURCE_REGEX.matcher(diskUrl);
            if (matcher.matches()) {
                String host = matcher.group("host");
                String url = HttpLink.HTTP_PREFIXES[0] + host + matcher.group("path");
                try {
                    links.add(LinkFactory.create(null, url, () -> matcher.group("pwd")));
                } catch (InvalidResourceException e) {
                    exceptions.add(e);
                }
            } else {
                exceptions.add(new UnknownResourceException("Not a disk resource", null, diskUrl));
            }
        }
    }

    private String decode(String urls) {
        urls = new StringBuilder(urls).reverse().toString();
        urls = new String(Base64.getDecoder().decode(urls), Constants.UTF_8);
        return new String(urls.getBytes(StandardCharsets.UTF_16), StandardCharsets.UTF_16);
    }

    private static class Lazy {

        private static final Pattern ITEM_URL_REGEX;
        private static final Pattern IMDB_INFO_REGEX = Pattern
            .compile("(title/? ?|((?i)imdb|Db).{0,4})(?<id>tt\\d+)");
        private static final Pattern VAR_REGEX = Pattern.compile(
            "var urls = \"(?<urls>[0-9A-Za-z+/=]*)\", " +
                "adsUrls = \"[0-9A-Za-z+/=]*\", " +
                "diskUrls = \"(?<disk>[0-9A-Za-z+/=]*)\", " +
                "scoreData = \"(?<imdb>tt\\d+)? ?###(?<db>\\d+)?\""
        );
        private static final Pattern DISK_RESOURCE_REGEX = Pattern.compile(
            "(\\+链接: )?(?<pwd>[0-9A-Za-z]{4})?" +
                "(\\|\\|(https?|ttps| https|whttps|\\|https)|\\|?https|\\s+\\|\\|https)" +
                "://(?<host>www\\.yun\\.cn|pan\\.baidu\\.com|pan\\.xunlei\\.com)" +
                "(?<path>/[\\w-./?=&]+)\\s*"
        );
        private static final Pattern URLS_SEPARATOR = Pattern.compile("#{3,4}\r\n|#{3,4}");
        private static final Pattern DISK_URLS_SEPARATOR = Pattern.compile("###?\r\n|###|\r\n");

        static {
            String types = Arrays.stream(BdMovieType.values()).map(BdMovieType::getText)
                .collect(Collectors.joining("|"));
            ITEM_URL_REGEX = Pattern.compile(
                "https?://www\\.(bd2020|bd-film)\\.com/(?<t>" + types + ")/(?<id>\\d+)\\.htm");
        }
    }
}
