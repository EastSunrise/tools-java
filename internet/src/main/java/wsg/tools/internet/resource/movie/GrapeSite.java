package wsg.tools.internet.resource.movie;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.IdentifiedIterableRepositoryImpl;
import wsg.tools.internet.base.impl.IntRangeIterableRepositoryImpl;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.LinkFactory;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.impl.Ed2kLink;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kingen
 * @see <a href="https://www.putaoys.com">Grape Video</a>
 * @since 2021/3/2
 */
public class GrapeSite extends BaseSite {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Pattern TIME_REGEX = Pattern.compile("发布时间：(?<s>\\d{4}-\\d{2}-\\d{2})");
    private static final Pattern YEAR_REGEX = Pattern.compile("◎年\\s*代\\s+(?<y>\\d{4})\\s*◎");
    private static final Pattern VOD_REGEX = Pattern.compile("tt\\d+/?|/vod/\\d+/?|/vod/[a-z]+/[a-z]+/?|/html/\\d+\\.html");
    private static final Pattern VOD_GENRE_HREF_REGEX = Pattern.compile("/vod/list/(?<t>[a-z]+)");
    private static final String BT_ATTACH = "url_btbtt";

    private static GrapeSite instance;

    private final IterableRepository<GrapeNewsItem> newsRepository = new IntRangeIterableRepositoryImpl<>(this::findNewsItem, 16132);

    private GrapeSite() {
        super("Grape Vod", new BasicHttpSession("putaoys.com"), GrapeSite::handleResponse);
    }

    public synchronized static GrapeSite getInstance() {
        if (instance == null) {
            instance = new GrapeSite();
        }
        return instance;
    }

    protected static String handleResponse(HttpResponse response) throws IOException {
        try {
            return DEFAULT_RESPONSE_HANDLER.handleResponse(response);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
                throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, e.getReasonPhrase());
            }
            throw e;
        }
    }

    /**
     * The repository including downloadable resources.
     */
    public IterableRepository<GrapeNewsItem> getNewsRepository() {
        return newsRepository;
    }

    /**
     * The repository including vod resource.
     */
    public IterableRepository<GrapeVodItem> getVodRepository(GrapeVodType type) throws HttpResponseException {
        Iterator<String> iterator = getAllVodIndexes(type).stream().map(i -> i.path).iterator();
        return new IdentifiedIterableRepositoryImpl<>(this::findVodItem, iterator);
    }

    public GrapeNewsItem findNewsItem(@Nonnull Integer id) throws HttpResponseException {
        RequestBuilder builder = builder0("/movie/%d.html", id);
        Document document = getDocument(builder, SnapshotStrategy.never());
        LocalDate releaseDate = LocalDate.parse(RegexUtils.matchesOrElseThrow(TIME_REGEX, document.selectFirst(".updatetime").text()).group("s"));
        GrapeNewsItem item = new GrapeNewsItem(id, builder.toString(), releaseDate);

        item.setTitle(document.selectFirst("div.news_title_all").selectFirst(CssSelector.TAG_H1).text().strip());
        Element info = document.selectFirst(".text");
        Matcher matcher = YEAR_REGEX.matcher(info.text());
        if (matcher.find()) {
            item.setYear(Integer.parseInt(matcher.group("y")));
        }

        Elements as = info.select(CssSelector.TAG_A);
        List<AbstractLink> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        for (Element a : as) {
            try {
                String href = a.attr(CssSelector.ATTR_HREF).strip();
                if (StringUtils.isBlank(href) || VOD_REGEX.matcher(href).matches()) {
                    continue;
                }
                if (href.contains(BT_ATTACH)) {
                    href = href.replace("thunder://url_btbtt", "http://51btbtt.com/attach-download");
                }
                int index = StringUtils.indexOf(href, Ed2kLink.ED2K_PREFIX);
                if (StringUtils.INDEX_NOT_FOUND != index) {
                    href = href.substring(index);
                }
                resources.add(LinkFactory.create(a.text().strip(), href, () -> {
                    Node node = a.nextSibling();
                    if (node instanceof TextNode) {
                        return LinkFactory.getPassword(((TextNode) node).text());
                    }
                    return null;
                }));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);
        return item;
    }

    private List<SimpleItem> getAllVodIndexes(GrapeVodType type) throws HttpResponseException {
        List<SimpleItem> items = new ArrayList<>();
        for (int page = 1; ; page++) {
            VodList vodList = this.getIndexes(type, page);
            items.addAll(vodList.items);
            if (vodList.currentPage >= vodList.pagesCount) {
                break;
            }
        }
        return items;
    }

    private VodList getIndexes(GrapeVodType type, int page) throws HttpResponseException {
        String arg = String.format("vod-type-id-%d-p-%d", type.getCode(), page);
        RequestBuilder builder = builder0("/index.php").addParameter("s", arg);
        Document document = getDocument(builder, SnapshotStrategy.always());
        VodList.VodListBuilder listBuilder = VodList.builder();

        Element prev = document.selectFirst(".short-page").child(0);
        String[] parts = ((TextNode) (prev.previousSibling())).text().split("/");
        listBuilder.currentPage(Integer.parseInt(parts[0].strip()));
        listBuilder.pagesCount(Integer.parseInt(parts[1].strip()));
        if (prev.is(CssSelector.TAG_A)) {
            listBuilder.prev(prev.attr(CssSelector.ATTR_HREF));
        }
        Element next = prev.nextElementSibling();
        if (next.is(CssSelector.TAG_A)) {
            listBuilder.next(next.attr(CssSelector.ATTR_HREF));
        }

        Elements lis = document.selectFirst("#contents").select(CssSelector.TAG_LI);
        List<SimpleItem> items = new LinkedList<>();
        for (Element li : lis) {
            String href = li.selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF);
            LocalDate date = LocalDate.parse(((TextNode) li.selectFirst(".long").nextSibling()).text().strip());
            SimpleItem.SimpleItemBuilder itemBuilder = SimpleItem.builder().path(href).update(date);
            items.add(itemBuilder.build());
        }
        listBuilder.items(items);

        return listBuilder.build();
    }

    public GrapeVodItem findVodItem(String path) throws HttpResponseException {
        RequestBuilder builder = builder0(path);
        Document document = getDocument(builder, SnapshotStrategy.never());

        Elements heads = document.selectFirst("ul.bread-crumbs").select(CssSelector.TAG_A);
        AssertUtils.requireEquals(heads.size(), 3);
        Matcher matcher = RegexUtils.matchesOrElseThrow(VOD_GENRE_HREF_REGEX, heads.get(1).attr(CssSelector.ATTR_HREF));
        GrapeVodGenre genre = EnumUtilExt.deserializeText(matcher.group("t"), GrapeVodGenre.class, false);
        LocalDateTime addTime = LocalDateTime.parse(document.selectFirst("#addtime").text().strip(), FORMATTER);
        GrapeVodItem item = new GrapeVodItem(builder.toString(), genre, addTime);

        Element div = document.selectFirst(".detail-title");
        Element h1 = div.selectFirst(CssSelector.TAG_H);
        item.setTitle(h1.text().strip());
        Element span = h1.nextElementSibling();
        if (span.is(CssSelector.TAG_SPAN)) {
            int year = Integer.parseInt(StringUtils.strip(span.text(), "()"));
            item.setYear(year);
        }
        Map<String, Element> info = document.selectFirst(".info").select("dl").stream().collect(Collectors.toMap(Element::text, e -> e));
        Element dl = info.get("状态：");
        if (dl != null) {
            item.setState(dl.nextElementSibling().text().strip());
        }

        Elements lis = document.select("#downul").select(CssSelector.TAG_LI);
        List<AbstractLink> resources = new ArrayList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        for (Element li : lis) {
            Element input = li.selectFirst(CssSelector.TAG_INPUT);
            try {
                resources.add(LinkFactory.create(input.attr("file_name"), input.val()));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);

        return item;
    }

    @Builder
    static class VodList {
        List<SimpleItem> items;
        int currentPage;
        int pagesCount;
        String prev;
        String next;
    }

    @Builder
    static class SimpleItem {
        String path;
        LocalDate update;
    }
}
