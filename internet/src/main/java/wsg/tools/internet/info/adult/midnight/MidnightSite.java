package wsg.tools.internet.info.adult.midnight;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.function.TriFunction;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.info.adult.AdultEntry;
import wsg.tools.internet.info.adult.AdultEntryUtils;

/**
 * @author Kingen
 * @see <a href="https://www.shenyequ.com/">Midnight Zone</a>
 * @since 2021/2/22
 */
public final class MidnightSite extends BaseSite {

    private static final String IMG_HOST = "https://syqpic.hantangrx.com";
    private static final String MG_STAGE_HOST = "https://image.mgstage.com/";
    private static final Pattern IMG_FILE_HREF_REGEX =
        Pattern.compile("/d/file/\\d{4}-\\d{2}-\\d{2}/[a-z0-9]{32}\\.(jpg|gif)");
    private static final Map<MidnightType, Pattern> ITEM_URL_REGEXES = Arrays
        .stream(MidnightType.values()).collect(Collectors.toMap(type -> type, type -> Pattern
            .compile("https://www\\.shenyequ\\.com/" + type.getText() + "/(?<id>\\d+).html")));
    private static final String NAV_NAVIGATION = "nav.navigation";

    public MidnightSite() {
        super("Midnight", new BasicHttpSession("shenyequ.com"));
    }

    /**
     * Obtains the page of simple items by the given type.
     *
     * @return the page of the simple items
     */
    public MidnightPageResult findAllSimples(@Nonnull MidnightType type,
        @Nonnull MidnightPageRequest pageRequest) throws HttpResponseException {
        RequestBuilder builder = builder0("/e/action/ListInfo.php")
            .addParameter("page", pageRequest.getCurrent())
            .addParameter("classid", type.getCode())
            .addParameter("line", pageRequest.getPageSize())
            .addParameter("tempid", "11")
            .addParameter("orderby", pageRequest.getOrderBy().getText())
            .addParameter("myorder", 0);
        Document document = getDocument(builder, SnapshotStrategy.always());
        List<MidnightSimpleItem> items = new ArrayList<>();
        Elements lis = document.selectFirst("div[role=main]").select(CssSelectors.TAG_LI);
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_A);
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(ITEM_URL_REGEXES.get(type), href);
            int id = Integer.parseInt(matcher.group("id"));
            String title = a.attr(CssSelectors.ATTR_TITLE);
            String time = li.selectFirst(CssSelectors.TAG_TIME).text().strip();
            LocalDateTime release = DocumentUtils.parseInterval(time);
            items.add(new MidnightSimpleItem(id, title, release));
        }
        Element nav = document.selectFirst(NAV_NAVIGATION);
        int total = Integer.parseInt(nav.selectFirst("a[title=总数]").selectFirst("b").text());
        return new MidnightPageResult(items, pageRequest, total);
    }

    public MidnightActress findActress(int id) throws HttpResponseException {
        Pair<MidnightActress, List<Element>> pair =
            initItem(MidnightType.ACTRESS, id, MidnightActress::new);
        Map<String, String> works = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (Element content : pair.getRight()) {
            Element current =
                content.selectFirst(NAV_NAVIGATION).previousElementSibling()
                    .previousElementSibling();
            while (current != null) {
                Elements elements = current.select(CssSelectors.TAG_IMG);
                for (Element img : elements) {
                    String code = img.attr(CssSelectors.ATTR_ALT).strip();
                    if (code.endsWith(".jpg")) {
                        code = code.substring(0, code.length() - 4);
                    }
                    Matcher matcher = IMG_FILE_HREF_REGEX
                        .matcher(img.attr(CssSelectors.ATTR_SRC));
                    if (StringUtils.isNotBlank(code)) {
                        works.put(code, matcher.find() ? IMG_HOST + matcher.group() : null);
                    }
                }
                current = current.previousElementSibling();
            }
        }
        MidnightActress actress = pair.getLeft();
        actress.setWorks(works);
        return actress;
    }

    public MidnightAlbum findAlbum(int id) throws HttpResponseException {
        Pair<MidnightAlbum, List<Element>> pair =
            initItem(MidnightType.ALBUM, id, MidnightAlbum::new);
        MidnightAlbum album = pair.getLeft();
        album.setImages(getImages(pair.getRight()));
        return album;
    }

    public MidnightEntry findAdultEntry(@Nonnull MidnightEntryType type, int id)
        throws HttpResponseException {
        Pair<MidnightEntry, List<Element>> pair =
            initItem(type.getType(), id, MidnightEntry::new);
        List<Element> contents = pair.getRight();
        List<String> images = Objects.requireNonNull(getImages(contents));
        List<String> texts = new ArrayList<>();
        Element nav = contents.get(0).selectFirst(NAV_NAVIGATION);
        Node current = nav.previousElementSibling().previousSibling();
        while (current != null) {
            texts.addAll(DocumentUtils.collectTexts(current));
            current = current.previousSibling();
        }
        Map<String, String> info = new HashMap<>(8);
        for (String text : texts) {
            String[] parts = StringUtils.split(text, ":：", 2);
            if (parts.length < 2) {
                continue;
            }
            String[] parts0 = parts[0].split(" ");
            String key = parts0[parts0.length - 1];
            MapUtilsExt.putIfAbsent(info, key, parts[1]);
        }
        AdultEntry entry = AdultEntryUtils.getAdultEntry(info, images.get(0), ", ");
        MidnightEntry item = pair.getLeft();
        item.setImages(images);
        item.setEntry(entry);
        return item;
    }

    private <T extends BaseMidnightItem> Pair<T, List<Element>> initItem(@Nonnull MidnightType type,
        int id, @Nonnull TriFunction<Integer, String, LocalDateTime, T> constructor)
        throws HttpResponseException {
        RequestBuilder builder = builder0("/%s/%d.html", type.getText(), id);
        Document document = getDocument(builder, SnapshotStrategy.never());
        String datetime = document.selectFirst("time.data-time").text();
        LocalDateTime release = LocalDateTime.parse(datetime, Constants.DATE_TIME_FORMATTER);
        String title = document.selectFirst("h1.title").text();
        T t = constructor.apply(id, title, release);
        String keywords = document.selectFirst(CssSelectors.META_KEYWORDS)
            .attr(CssSelectors.ATTR_CONTENT);
        if (StringUtils.isNotBlank(keywords)) {
            t.setKeywords(keywords.split(","));
        }
        return Pair.of(t, getContents(document));
    }

    private List<Element> getContents(Document document) throws HttpResponseException {
        List<Element> contents = new ArrayList<>();
        while (true) {
            Element content = document.selectFirst("div.single-content");
            contents.add(content);
            Element next = content.selectFirst(NAV_NAVIGATION).selectFirst("a.next");
            if (next == null) {
                break;
            }
            String nextHref = next.attr(CssSelectors.ATTR_HREF);
            RequestBuilder builder = builder0(URI.create(nextHref).getPath());
            document = getDocument(builder, SnapshotStrategy.never());
        }
        return contents;
    }

    private List<String> getImages(List<Element> contents) {
        List<String> images = new ArrayList<>();
        for (Element content : contents) {
            Element nav = content.selectFirst(NAV_NAVIGATION);
            Node current = nav.previousElementSibling().previousSibling();
            while (current != null) {
                if (current instanceof Element) {
                    Elements elements = ((Element) current).select(CssSelectors.TAG_IMG);
                    for (Element img : elements) {
                        String href = img.attr(CssSelectors.ATTR_SRC);
                        if (href.startsWith(MG_STAGE_HOST)) {
                            current = current.previousSibling();
                            continue;
                        }
                        Matcher matcher = IMG_FILE_HREF_REGEX.matcher(href);
                        if (matcher.matches()) {
                            href = IMG_HOST + href;
                        }
                        images.add(href);
                    }
                }
                current = current.previousSibling();
            }
        }
        return images.isEmpty() ? null : images;
    }
}
