package wsg.tools.internet.resource.site;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.BasicHttpSession;
import wsg.tools.internet.base.RequestBuilder;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.intf.IntRangeRepository;
import wsg.tools.internet.base.intf.IntRangeRepositoryImpl;
import wsg.tools.internet.base.intf.RangeRepository;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.impl.Ed2kResource;
import wsg.tools.internet.resource.impl.ResourceFactory;
import wsg.tools.internet.resource.item.VideoType;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    private static final String BT_ATTACH = "url_btbtt";

    private static GrapeSite instance;

    /**
     * The repository including downloadable resources.
     */
    @Getter
    private final IntRangeRepository<GrapeNewsItem> newsRepository = new IntRangeRepositoryImpl<>(this::findNewsItem, () -> 16132);

    /**
     * The repository including vod resource.
     */
    @Getter
    private final RangeRepository<GrapeVodItem, LocalDate> vodRepository = new RangeRepository<>() {
        @Nonnull
        @Override
        public LocalDate min() {
            return LocalDate.of(2001, 9, 11);
        }

        @Nonnull
        @Override
        public LocalDate max() {
            return LocalDate.now();
        }

        @Override
        public List<GrapeVodItem> findAllByRangeClosed(@Nonnull LocalDate startInclusive, @Nonnull LocalDate endInclusive) throws HttpResponseException {
            List<GrapeVodItem> vodItems = new LinkedList<>();
            vodItems.addAll(getRangedVodItems(1, startInclusive, endInclusive, VideoType.MOVIE));
            vodItems.addAll(getRangedVodItems(2, startInclusive, endInclusive, VideoType.SERIES));
            vodItems.addAll(getRangedVodItems(3, startInclusive, endInclusive, VideoType.ANIME));
            vodItems.addAll(getRangedVodItems(4, startInclusive, endInclusive, VideoType.VARIETY));
            return vodItems;
        }
    };

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

    private GrapeNewsItem findNewsItem(@Nonnull Integer id) throws HttpResponseException {
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
        List<AbstractResource> resources = new LinkedList<>();
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
                int index = StringUtils.indexOf(href, Ed2kResource.ED2K_PREFIX);
                if (StringUtils.INDEX_NOT_FOUND != index) {
                    href = href.substring(index);
                }
                resources.add(ResourceFactory.create(a.text().strip(), href, () -> {
                    Node node = a.nextSibling();
                    if (node instanceof TextNode) {
                        return ResourceFactory.getPassword(((TextNode) node).text());
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

    private List<GrapeVodItem> getRangedVodItems(int typeId, LocalDate start, LocalDate end, VideoType type) throws HttpResponseException {
        List<GrapeVodItem> items = new LinkedList<>();
        for (int page = 1; ; page++) {
            VodList vodList = this.getVodList(typeId, page);
            for (SimpleItem item : vodList.items) {
                if (item.update.isAfter(end)) {
                    continue;
                }
                if (item.update.isBefore(start)) {
                    return items;
                }
                items.add(getVodItem(item.path, type));
            }
            if (vodList.currentPage >= vodList.pagesCount) {
                break;
            }
        }
        return items;
    }

    private VodList getVodList(int typeId, int page) throws HttpResponseException {
        RequestBuilder builder = builder0("/index.php").addParameter("s", String.format("vod-type-id-%d-p-%d.html", typeId, page));
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

    private GrapeVodItem getVodItem(String path, VideoType type) throws HttpResponseException {
        RequestBuilder builder = builder0(path);
        Document document = getDocument(builder, SnapshotStrategy.never());

        LocalDateTime addTime = LocalDateTime.parse(document.selectFirst("#addtime").text().strip(), FORMATTER);
        GrapeVodItem item = new GrapeVodItem(builder.toString(), type, addTime);
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
        List<AbstractResource> resources = new ArrayList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        for (Element li : lis) {
            Element input = li.selectFirst(CssSelector.TAG_INPUT);
            try {
                resources.add(ResourceFactory.create(input.attr("file_name"), input.val()));
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
