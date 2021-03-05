package wsg.tools.internet.info.adult.midnight;

import java.net.URI;
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
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.IdentifiedIterableRepositoryImpl;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.info.adult.AdultEntry;
import wsg.tools.internet.info.adult.AdultEntryUtils;

/**
 * @author Kingen
 * @see <a href="https://www.shenyequ.com/">Midnight Zone</a>
 * @since 2021/2/22
 */
public final class MidnightSite extends BaseSite {

    private static final String HASH = "#";
    private static final String IMG_HOST = "https://syqpic.hantangrx.com";
    private static final String MG_STAGE_HOST = "https://image.mgstage.com/";
    private static final Pattern IMG_FILE_HREF_REGEX =
        Pattern.compile("/d/file/\\d{4}-\\d{2}-\\d{2}/[a-z0-9]{32}\\.(jpg|gif)");
    private static final Map<MidnightType, Pattern> ITEM_URL_REGEXES =
        Arrays.stream(MidnightType.values()).collect(Collectors.toMap(type -> type,
            type -> Pattern
                .compile("https://www\\.shenyequ\\.com/" + type.getText() + "/(?<id>\\d+).html")));

    public MidnightSite() {
        super("Midnight", new BasicHttpSession("shenyequ.com"));
    }

    public IterableRepository<MidnightWrapper<MidnightActress>> getActressRepository()
        throws HttpResponseException {
        return new IdentifiedIterableRepositoryImpl<>(this::findActress,
            getIdentifierIterator(MidnightType.ACTRESS));
    }

    public IterableRepository<MidnightWrapper<MidnightAlbum>> getAlbumRepository()
        throws HttpResponseException {
        return new IdentifiedIterableRepositoryImpl<>(this::findAlbum,
            getIdentifierIterator(MidnightType.ALBUM));
    }

    public IterableRepository<MidnightWrapper<MidnightEntry>> getEntryRepository(
        @Nonnull MidnightEntryType type)
        throws HttpResponseException {
        return new IdentifiedIterableRepositoryImpl<>(integer -> findAdultEntry(type, integer),
            getIdentifierIterator(type.getType()));
    }

    public MidnightWrapper<MidnightActress> findActress(int id) throws HttpResponseException {
        return initWrapper(MidnightType.ACTRESS, id, (contents, title) -> {
            MidnightActress actress = new MidnightActress(title);
            Map<String, String> works = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
            for (Element content : contents) {
                Element current =
                    content.selectFirst("nav.navigation").previousElementSibling()
                        .previousElementSibling();
                while (current != null) {
                    Elements elements = current.select("img");
                    for (Element img : elements) {
                        String code = img.attr("alt").strip();
                        if (code.endsWith(".jpg")) {
                            code = code.substring(0, code.length() - 4);
                        }
                        Matcher matcher = IMG_FILE_HREF_REGEX
                            .matcher(img.attr(CssSelector.ATTR_SRC));
                        if (StringUtils.isNotBlank(code)) {
                            works.put(code, matcher.find() ? IMG_HOST + matcher.group() : null);
                        }
                    }
                    current = current.previousElementSibling();
                }
            }
            actress.setWorks(works);
            return actress;
        });
    }

    public MidnightWrapper<MidnightAlbum> findAlbum(int id) throws HttpResponseException {
        return initWrapper(MidnightType.ALBUM, id, (contents, title) -> {
            MidnightAlbum album = new MidnightAlbum(title);
            album.setImages(getImages(contents));
            return album;
        });
    }

    public MidnightWrapper<MidnightEntry> findAdultEntry(@Nonnull MidnightEntryType type, int id)
        throws HttpResponseException {
        return initWrapper(type.getType(), id, (contents, title) -> {
            List<String> images = Objects.requireNonNull(getImages(contents));
            Node current = contents.get(0).selectFirst("nav.navigation").previousElementSibling()
                .previousSibling();
            List<String> texts = new ArrayList<>();
            for (; current != null; current = current.previousSibling()) {
                collectTexts(current, texts);
            }
            Map<String, String> info = texts.stream().map(s -> StringUtils.split(s, ":：", 2))
                .filter(ss -> ss.length > 1).collect(Collectors.toMap(ss -> {
                    String[] parts = ss[0].split(" ");
                    return parts[parts.length - 1];
                }, ss -> ss[1].strip()));
            AdultEntry entry = AdultEntryUtils.getAdultEntry(info, images.get(0), ", ");
            MidnightEntry item = new MidnightEntry(title);
            item.setImages(images);
            item.setEntry(entry);
            return item;
        });
    }

    private <T extends TitleSupplier> MidnightWrapper<T> initWrapper(@Nonnull MidnightType type,
        int id,
        @Nonnull BiFunction<List<Element>, String, T> getContent) throws HttpResponseException {
        Document document = getDocument(builder0("/%s/%d.html", type.getText(), id),
            SnapshotStrategy.never());
        LocalDateTime release =
            LocalDateTime.parse(document.selectFirst("time.data-time").text(),
                Constants.STANDARD_DATE_TIME_FORMATTER);
        String title = document.selectFirst("h1.title").text();
        String keywords = document.selectFirst("meta[name=keywords]")
            .attr(CssSelector.ATTR_CONTENT);
        List<Element> contents = new ArrayList<>();
        while (true) {
            Element content = document.selectFirst("div.single-content");
            contents.add(content);
            Element next = content.selectFirst("div.pagination").selectFirst("a.next");
            if (next == null) {
                break;
            }
            document =
                getDocument(builder0(URI.create(next.attr(CssSelector.ATTR_HREF)).getPath()),
                    SnapshotStrategy.never());
        }
        T t = getContent.apply(contents, title);
        MidnightWrapper<T> wrapper = new MidnightWrapper<>(id, release, t);
        wrapper.setKeywords(StringUtils.isBlank(keywords) ? null : keywords.split(","));
        wrapper.setNext(getNext(document, type));
        return wrapper;
    }

    private void collectTexts(Node node, List<String> texts) {
        if (node instanceof TextNode) {
            String text = ((TextNode) node).text();
            if (StringUtils.isBlank(text)) {
                return;
            }
            texts.add(text.strip());
            return;
        }
        if (node instanceof Element) {
            for (Node childNode : node.childNodes()) {
                collectTexts(childNode, texts);
            }
            return;
        }
        throw new IllegalArgumentException("Unexpected type of node: " + node.getClass());
    }

    private List<String> getImages(List<Element> contents) {
        List<String> images = new ArrayList<>();
        for (Element content : contents) {
            Node current = content.selectFirst("nav.navigation").previousElementSibling().previousSibling();
            for (; current != null; current = current.previousSibling()) {
                if (current instanceof Element) {
                    Elements elements = ((Element) current).select("img");
                    for (Element img : elements) {
                        String href = img.attr(CssSelector.ATTR_SRC);
                        if (href.startsWith(MG_STAGE_HOST)) {
                            continue;
                        }
                        Matcher matcher = IMG_FILE_HREF_REGEX.matcher(href);
                        if (matcher.matches()) {
                            href = IMG_HOST + href;
                        }
                        images.add(href);
                    }
                }
            }
        }
        return images.isEmpty() ? null : images;
    }

    private Integer getNext(Document document, MidnightType type) {
        String next = document.selectFirst("a.next-post").attr(CssSelector.ATTR_HREF);
        if (HASH.equals(next)) {
            return null;
        }
        return Integer.parseInt(RegexUtils.matchesOrElseThrow(ITEM_URL_REGEXES.get(type), next).group("id"));
    }

    /**
     * Obtains the list of the identifiers of the records by the given type.
     *
     * @return the list of the identifiers
     */
    private List<Integer> getIdentifierIterator(MidnightType type) throws HttpResponseException {
        List<Integer> ids = new ArrayList<>();
        int page = 0;
        RequestBuilder builder =
            builder0("/e/action/ListInfo.php")
                .addParameter("classid", String.valueOf(type.getCode()))
                .addParameter("tempid", "11").addParameter("orderby", "newstime")
                .addParameter("line", "80");
        while (true) {
            Document document =
                getDocument(builder.setParameter("page", String.valueOf(page)),
                    SnapshotStrategy.always());
            page++;
            Elements lis = document.selectFirst("div[role=main]").select(CssSelector.TAG_LI);
            for (Element li : lis) {
                String href = li.selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF);
                ids.add(Integer.parseInt(
                    RegexUtils.matchesOrElseThrow(ITEM_URL_REGEXES.get(type), href).group("id")));
            }
            Element next = document.selectFirst("div.pagination").selectFirst("a.next");
            if (null == next) {
                break;
            }
        }
        return ids;
    }
}
