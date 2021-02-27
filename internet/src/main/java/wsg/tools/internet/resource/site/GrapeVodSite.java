package wsg.tools.internet.resource.site;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.CssSelector;
import wsg.tools.internet.base.RangeRepository;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.impl.ResourceFactory;
import wsg.tools.internet.resource.item.VideoType;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Access to items of Grape Video, which are updating.
 *
 * @author Kingen
 * @since 2021/2/4
 */
public class GrapeVodSite extends BaseSite implements RangeRepository<GrapeVodItem, LocalDate> {

    private static final LocalDate START_DATE = LocalDate.of(2014, 1, 1);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static GrapeVodSite instance;

    private GrapeVodSite() {
        super("Grape Vod", "putaoys.com");
    }

    public static GrapeVodSite getInstance() {
        if (instance == null) {
            instance = new GrapeVodSite();
        }
        return instance;
    }

    @Override
    public List<GrapeVodItem> findAllByRangeClosed(@Nullable LocalDate startInclusive, @Nullable LocalDate endInclusive) throws HttpResponseException {
        if (startInclusive == null || startInclusive.isBefore(START_DATE)) {
            startInclusive = START_DATE;
        }
        if (endInclusive == null) {
            endInclusive = LocalDate.now();
        }
        List<GrapeVodItem> vodItems = new LinkedList<>();
        vodItems.addAll(getRangedVodItems(1, startInclusive, endInclusive, VideoType.MOVIE));
        vodItems.addAll(getRangedVodItems(2, startInclusive, endInclusive, VideoType.TV));
        vodItems.addAll(getRangedVodItems(3, startInclusive, endInclusive, VideoType.ANIME));
        vodItems.addAll(getRangedVodItems(4, startInclusive, endInclusive, VideoType.VARIETY));
        return vodItems;
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
        URIBuilder builder = builder0("/index.php").addParameter("s", String.format("vod-type-id-%d-p-%d.html", typeId, page));
        Document document = getDocument(builder, SnapshotStrategy.ALWAYS_UPDATE);
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
        URIBuilder builder = builder0(path);
        Document document = getDocument(builder, SnapshotStrategy.NEVER_UPDATE);

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

    @Override
    public String handleResponse(HttpResponse response) throws IOException {
        try {
            return super.handleResponse(response);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
                throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, e.getReasonPhrase());
            }
            throw e;
        }
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
