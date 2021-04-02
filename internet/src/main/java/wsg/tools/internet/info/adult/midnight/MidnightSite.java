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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TriFunction;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.RequestBuilder;
import wsg.tools.internet.base.support.SnapshotStrategies;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.info.adult.entry.AdultEntryBuilder;
import wsg.tools.internet.info.adult.entry.AmateurAdultEntry;
import wsg.tools.internet.info.adult.entry.FormalAdultEntry;

/**
 * @author Kingen
 * @see <a href="https://www.shenyequ.com/">Midnight Zone</a>
 * @since 2021/2/22
 */
@ConcreteSite
public final class MidnightSite extends BaseSite {

    private static final String NAV_NAVIGATION = "nav.navigation";

    public MidnightSite() {
        super("Midnight", new BasicHttpSession("shenyequ.com"));
    }

    /**
     * Retrieves the paged result of indices under the given column.
     */
    public MidnightPageResult findPage(@Nonnull MidnightColumn column,
        @Nonnull MidnightPageReq req) throws NotFoundException, OtherResponseException {
        RequestBuilder builder = builder0("/e/action/ListInfo.php")
            .addParameter("page", req.getCurrent())
            .addParameter("classid", column.getCode())
            .addParameter("starttime", req.getStart())
            .addParameter("endtime", req.getEnd())
            .addParameter("line", req.getPageSize())
            .addParameter("tempid", "11")
            .addParameter("orderby", req.getOrderBy().getText())
            .addParameter("myorder", 0);
        Document document = getDocument(builder, SnapshotStrategies.always());
        List<MidnightIndex> indices = new ArrayList<>();
        Elements lis = document.selectFirst("div[role=main]").select(CssSelectors.TAG_LI);
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_A);
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.ITEM_URL_REGEX, href);
            int id = Integer.parseInt(matcher.group("id"));
            String title = a.attr(CssSelectors.ATTR_TITLE);
            String time = li.selectFirst(CssSelectors.TAG_TIME).text().strip();
            LocalDateTime release = DocumentUtils.parseInterval(time);
            indices.add(new MidnightIndex(id, title, release));
        }
        Element nav = document.selectFirst(NAV_NAVIGATION);
        int total = Integer.parseInt(nav.selectFirst("a[title=总数]").selectFirst("b").text());
        return new MidnightPageResult(indices, req, total);
    }

    /**
     * Retrieves an item with a collection of adult entries.
     */
    @Nonnull
    public MidnightCollection findCollection(int id)
        throws NotFoundException, OtherResponseException {
        return getItem(MidnightColumn.COLLECTION, id, (title, release, contents) -> {
            List<Pair<String, URL>> works = new ArrayList<>();
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
                        works.add(Pair.of(code, NetUtils.createURL(src)));
                    }
                    current = current.previousElementSibling();
                }
            }
            return new MidnightCollection(id, title, release, works);
        });
    }

    /**
     * Retrieves an item with images.
     */
    @Nonnull
    public MidnightAlbum findAlbum(int id) throws NotFoundException, OtherResponseException {
        return getItem(MidnightColumn.ALBUM, id,
            (title, release, contents) -> new MidnightAlbum(id, title, release,
                getImages(contents)));
    }

    /**
     * Retrieves an item with an amateur adult entry.
     */
    public MidnightAmateurEntry findAmateurEntry(@Nonnull MidnightAmateurColumn type, int id)
        throws NotFoundException, OtherResponseException {
        return getItem(type.getColumn(), id, (title, release, contents) -> {
            List<URL> images = getImages(contents);
            Map<String, String> info = getInfo(contents);
            String code = AdultEntryBuilder.getCode(info);
            if (code == null) {
                code = title;
            }
            AmateurAdultEntry entry = AdultEntryBuilder.builder(code, info)
                .duration().producer().release().series().distributor()
                .images(images).ignore("商品発売日").amateur();
            return new MidnightAmateurEntry(id, title, release, entry);
        });
    }

    /**
     * Retrieves an item with a formal adult entry.
     */
    @Nonnull
    public MidnightFormalAdultEntry findFormalEntry(int id)
        throws NotFoundException, OtherResponseException {
        return getItem(MidnightColumn.ENTRY, id, (title, release, contents) -> {
            List<URL> images = Objects.requireNonNull(getImages(contents));
            Map<String, String> info = getInfo(contents);
            String code = AdultEntryBuilder.getCode(info);
            if (code == null) {
                code = title;
            }
            FormalAdultEntry entry = AdultEntryBuilder.builder(code, info)
                .duration().release().producer().distributor().series()
                .images(images).ignoreAllRemaining().formal(", ");
            return new MidnightFormalAdultEntry(id, title, release, entry);
        });
    }

    /**
     * Retrieves an item.
     *
     * @param column      column that the item belongs to
     * @param id          the id of target item
     * @param constructor constructs an item with title, release, and the main content in the
     *                    document as arguments
     */
    private <T extends BaseMidnightItem> T getItem(@Nonnull MidnightColumn column,
        int id, @Nonnull TriFunction<String, LocalDateTime, List<Element>, T> constructor)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = builder0("/%s/%d.html", column.getText(), id);
        Document document = getDocument(builder, SnapshotStrategies.never());
        String datetime = document.selectFirst("time.data-time").text();
        LocalDateTime release = LocalDateTime.parse(datetime, Constants.YYYY_MM_DD_HH_MM_SS);
        String title = document.selectFirst("h1.title").text();
        T t = constructor.apply(title, release, getContents(document));
        String keywords = DocumentUtils.getMetadata(document).get("keywords");
        if (StringUtils.isNotBlank(keywords)) {
            t.setKeywords(keywords.split(","));
        }
        return t;
    }

    private List<Element> getContents(Document document)
        throws NotFoundException, OtherResponseException {
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
            document = getDocument(builder, SnapshotStrategies.never());
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
            String[] parts0 = parts[0].split(Constants.WHITESPACE, 2);
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
                        if (href.startsWith(Constants.URL_PATH_SEPARATOR)) {
                            href = builder0(href).toString();
                        }
                        images.add(NetUtils.createURL(href));
                    }
                }
                current = current.previousSibling();
            }
        }
        return images.isEmpty() ? null : images;
    }

    private static class Lazy {

        private static final Pattern ITEM_URL_REGEX;

        static {
            String columns = Arrays.stream(MidnightColumn.values()).map(TextSupplier::getText)
                .collect(Collectors.joining("|"));
            ITEM_URL_REGEX = Pattern.compile(
                "https://www\\.shenyequ\\.com/(?<t>" + columns + ")/(?<id>\\d+).html");
        }
    }
}
