package wsg.tools.internet.info.adult.midnight;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
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
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.function.TriFunction;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.Repositories;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.IntIndicesRepository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.info.adult.common.AdultEntry;
import wsg.tools.internet.info.adult.common.AdultEntryBuilder;

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
    private static final Map<MidnightColumn, Pattern> ITEM_URL_REGEXES = Arrays
        .stream(MidnightColumn.values()).collect(Collectors.toMap(type -> type, type -> Pattern
            .compile("https://www\\.shenyequ\\.com/" + type.getText() + "/(?<id>\\d+).html")));
    private static final String NAV_NAVIGATION = "nav.navigation";

    public MidnightSite() {
        super("Midnight", new BasicHttpSession("shenyequ.com"));
    }

    /**
     * Returns the repository of all {@link MidnightCollection}s.
     */
    public IntIndicesRepository<MidnightCollection> getCollectionRepository()
        throws HttpResponseException {
        List<Integer> ids = getIdentifiers(MidnightColumn.COLLECTION);
        return Repositories.intIndices(this::findCollection, ids);
    }

    /**
     * Returns the repository of all {@link MidnightAlbum}s.
     */
    public IntIndicesRepository<MidnightAlbum> getAlbumRepository()
        throws HttpResponseException {
        List<Integer> ids = getIdentifiers(MidnightColumn.ALBUM);
        return Repositories.intIndices(this::findAlbum, ids);
    }

    /**
     * Returns the repository of all items each of which may contain a amateur adult entry.
     */
    public IntIndicesRepository<BaseMidnightEntry> getAmateurRepository(
        @Nonnull MidnightAmateurEntryType type) throws HttpResponseException {
        List<Integer> ids = getIdentifiers(type.getColumn());
        return Repositories.intIndices(id -> findAmateurEntry(type, id), ids);
    }

    /**
     * Returns the repository of all items each of which may contain a formal adult entry.
     */
    public IntIndicesRepository<BaseMidnightEntry> getFormalRepository()
        throws HttpResponseException {
        List<Integer> ids = getIdentifiers(MidnightColumn.ENTRY);
        return Repositories.intIndices(this::findFormalEntry, ids);
    }

    private List<Integer> getIdentifiers(MidnightColumn column) throws HttpResponseException {
        List<Integer> ids = new ArrayList<>();
        MidnightPageRequest request = MidnightPageRequest.first();
        while (true) {
            MidnightPageResult result = findAllIndices(column, request);
            result.getContent().stream().map(MidnightIndex::getId).forEach(ids::add);
            if (!result.hasNext()) {
                break;
            }
            request = result.nextPageRequest();
        }
        return ids;
    }

    /**
     * Finds the paged result of indices under the given column.
     */
    public MidnightPageResult findAllIndices(@Nonnull MidnightColumn column,
        @Nonnull MidnightPageRequest pageRequest) throws HttpResponseException {
        RequestBuilder builder = builder0("/e/action/ListInfo.php")
            .addParameter("page", pageRequest.getCurrent())
            .addParameter("classid", column.getCode())
            .addParameter("line", pageRequest.getPageSize())
            .addParameter("tempid", "11")
            .addParameter("orderby", pageRequest.getOrderBy().getText())
            .addParameter("myorder", 0);
        Document document = getDocument(builder, SnapshotStrategy.always());
        List<MidnightIndex> indices = new ArrayList<>();
        Elements lis = document.selectFirst("div[role=main]").select(CssSelectors.TAG_LI);
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_A);
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(ITEM_URL_REGEXES.get(column), href);
            int id = Integer.parseInt(matcher.group("id"));
            String title = a.attr(CssSelectors.ATTR_TITLE);
            String time = li.selectFirst(CssSelectors.TAG_TIME).text().strip();
            LocalDateTime release = DocumentUtils.parseInterval(time);
            indices.add(new MidnightIndex(id, title, release));
        }
        Element nav = document.selectFirst(NAV_NAVIGATION);
        int total = Integer.parseInt(nav.selectFirst("a[title=总数]").selectFirst("b").text());
        return new MidnightPageResult(indices, pageRequest, total);
    }

    /**
     * Finds an item with a collection of adult entries.
     */
    public MidnightCollection findCollection(int id)
        throws HttpResponseException {
        return getItem(MidnightColumn.COLLECTION, id, (title, release, contents) -> {
            List<Pair<String, String>> works = new ArrayList<>();
            for (Element content : contents) {
                Element nav = content.selectFirst(NAV_NAVIGATION);
                Element current = nav.previousElementSibling().previousElementSibling();
                while (current != null) {
                    Elements elements = current.select(CssSelectors.TAG_IMG);
                    for (Element img : elements) {
                        String code = img.attr(CssSelectors.ATTR_ALT).strip();
                        if (code.endsWith(".jpg")) {
                            code = code.substring(0, code.length() - 4);
                        }
                        if (StringUtils.isBlank(code)) {
                            code = null;
                        }
                        String src = img.attr(CssSelectors.ATTR_SRC);
                        Matcher matcher = IMG_FILE_HREF_REGEX.matcher(src);
                        if (matcher.find()) {
                            src = IMG_HOST + matcher.group();
                        } else {
                            src = null;
                        }
                        works.add(Pair.of(code, src));
                    }
                    current = current.previousElementSibling();
                }
            }
            return new MidnightCollection(id, title, release, works);
        });
    }

    /**
     * Finds an item with an album which including a series of images.
     */
    public MidnightAlbum findAlbum(int id) throws HttpResponseException {
        return getItem(MidnightColumn.ALBUM, id,
            (title, release, contents) -> new MidnightAlbum(id, title, release,
                getImages(contents)));
    }

    /**
     * Finds an item with an album or a amateur adult entry.
     */
    public BaseMidnightEntry findAmateurEntry(@Nonnull MidnightAmateurEntryType type, int id)
        throws HttpResponseException {
        return findEntry(type.getColumn(), id, AdultEntryBuilder::amateur);
    }

    /**
     * Finds an item with an album or a formal adult entry.
     */
    public BaseMidnightEntry findFormalEntry(int id) throws HttpResponseException {
        return findEntry(MidnightColumn.ENTRY, id,
            (info, code) -> AdultEntryBuilder.formal(info, code, ", "));
    }

    /**
     * Finds an item with an album or an adult entry.
     */
    private BaseMidnightEntry findEntry(MidnightColumn column, int id,
        BiFunction<Map<String, String>, String, AdultEntryBuilder.AdultEntryMapBuilder> builder)
        throws HttpResponseException {
        return getItem(column, id, (title, release, contents) -> {
            List<URL> images = Objects.requireNonNull(getImages(contents));
            Map<String, String> info = getInfo(contents);
            String code = AdultEntryBuilder.getCode(info);
            if (code == null) {
                return new MidnightAlbum(id, title, release, images);
            }
            info.remove("监督");
            AdultEntry entry = builder.apply(info, code)
                .duration().release().producer().distributor().series()
                .title(title).images(images).build();
            return new MidnightAdultEntry(id, title, release, entry);
        });
    }

    /**
     * Obtains an item.
     *
     * @param column      column that the item belongs to
     * @param id          the id of target item
     * @param constructor constructs an item with title, release, and the main content in the
     *                    document as arguments
     */
    private <T extends BaseMidnightItem> T getItem(@Nonnull MidnightColumn column,
        int id, @Nonnull TriFunction<String, LocalDateTime, List<Element>, T> constructor)
        throws HttpResponseException {
        RequestBuilder builder = builder0("/%s/%d.html", column.getText(), id);
        Document document = getDocument(builder, SnapshotStrategy.never());
        String datetime = document.selectFirst("time.data-time").text();
        LocalDateTime release = LocalDateTime.parse(datetime, Constants.YYYY_MM_DD_HH_MM_SS);
        String title = document.selectFirst("h1.title").text();
        T t = constructor.apply(title, release, getContents(document));
        String keywords = document.selectFirst(CssSelectors.META_KEYWORDS)
            .attr(CssSelectors.ATTR_CONTENT);
        if (StringUtils.isNotBlank(keywords)) {
            t.setKeywords(keywords.split(","));
        }
        return t;
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

    /**
     * Obtains a map of information from the contents.
     */
    private Map<String, String> getInfo(List<Element> contents) {
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
            String[] parts0 = parts[0].split(Constants.WHITESPACE);
            String key = parts0[parts0.length - 1];
            MapUtilsExt.putIfAbsent(info, key, parts[1]);
        }
        return info;
    }

    private List<URL> getImages(List<Element> contents) {
        List<URL> images = new ArrayList<>();
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
                        images.add(NetUtils.createURL(href));
                    }
                }
                current = current.previousSibling();
            }
        }
        return images.isEmpty() ? null : images;
    }
}
