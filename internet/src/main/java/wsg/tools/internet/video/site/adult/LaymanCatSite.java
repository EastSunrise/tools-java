package wsg.tools.internet.video.site.adult;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.BasicHttpSession;
import wsg.tools.internet.base.IterableRepositoryImpl;
import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.common.Scheme;
import wsg.tools.internet.common.WithoutNextDocument;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kingen
 * @see <a href="http://www.surenmao.com/">Layman Cat</a>
 * @since 2021/2/28
 */
public class LaymanCatSite extends BaseSite implements Repository<String, LaymanCatItem>, IterableRepository<LaymanCatItem> {

    private static final Pattern HREF_REGEX = Pattern.compile("http://www\\.surenmao\\.com/(?<id>[0-9a-z-]+)/");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    private static final String FIRST_KEY = "収録時間";
    private static LaymanCatSite instance;

    private final IterableRepository<LaymanCatItem> repository = new IterableRepositoryImpl<>(this, "200gana-1829");

    private LaymanCatSite() {
        super("Layman Cat", new BasicHttpSession(Scheme.HTTP, "surenmao.com"));
    }

    public synchronized static LaymanCatSite getInstance() {
        if (instance == null) {
            instance = new LaymanCatSite();
        }
        return instance;
    }

    @Override
    public RepositoryIterator<LaymanCatItem> iterator() {
        return repository.iterator();
    }

    @Override
    public LaymanCatItem findById(@Nonnull String id) throws HttpResponseException {
        Document document = getDocument(builder0("/%s/", id), new WithoutNextDocument<>(this::getNext));
        Element main = document.selectFirst("#main");
        String code = main.selectFirst("h1.entry-title").text();
        String cover = main.selectFirst("img.size-full").attr("src");
        LocalDateTime published = LocalDateTime.parse(main.selectFirst("time.published").attr("datetime"), DATETIME_FORMATTER);
        LocalDateTime updated = LocalDateTime.parse(main.selectFirst("time.updated").attr("datetime"), DATETIME_FORMATTER);
        String author = main.selectFirst("span.author").text();

        Elements children = main.selectFirst("div.entry-content").children();
        Map<String, List<Element>> map = children.stream().collect(Collectors.groupingBy(Element::tagName));
        if (map.size() == 1) {
            Element current = children.first();
            if (current.childNodeSize() == 1) {
                current = current.nextElementSibling();
            }
            StringBuilder description = new StringBuilder(current.text());
            current = current.nextElementSibling();
            if (current == null) {
                return new LaymanCatItem(id, new AdultEntry(code, cover), author, published, updated, description.toString(), getNext(document));
            }
            while (current.childNodeSize() == 1) {
                description.append(current.text());
                current = current.nextElementSibling();
            }
            AdultEntry entry = getEntry(code, cover, current.childNodes().stream().filter(n -> n instanceof TextNode).map(n -> (TextNode) n)
                    .map(TextNode::text).collect(Collectors.toList()));
            return new LaymanCatItem(id, entry, author, published, updated, description.toString(), getNext(document));
        }
        if (map.containsKey(CssSelector.TAG_DIV)) {
            AdultEntry entry = getEntry(code, cover, map.get(CssSelector.TAG_DIV).stream().map(Element::text).collect(Collectors.toList()));
            return new LaymanCatItem(id, entry, author, published, updated, children.get(1).text(), getNext(document));
        }
        return new LaymanCatItem(id, new AdultEntry(code, cover), author, published, updated, getNext(document));
    }

    private String getNext(Document document) {
        Element next = document.selectFirst("div.nav-next");
        if (next == null) {
            return null;
        }
        String href = next.selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF);
        return RegexUtils.matchesOrElseThrow(HREF_REGEX, href).group("id");
    }

    private AdultEntry getEntry(@Nonnull String code, @Nonnull String cover, List<String> lines) {
        Map<String, String> info = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        Iterator<String> iterator = lines.iterator();
        String[] parts = StringUtils.split(StringUtils.stripStart(iterator.next(), " ・"), ":：", 2);
        if (FIRST_KEY.equals(parts[0])) {
            parts[1] = parts[1].split("・")[0];
        }
        info.put(parts[0], parts[1].strip());
        while (iterator.hasNext()) {
            String[] kv = StringUtils.split(StringUtils.stripStart(iterator.next(), " ・"), ":：", 2);
            if (kv.length == 2) {
                info.put(kv[0], kv[1].strip());
            }
        }
        return AdultEntryUtils.getAdultEntry(info, code, cover, " ");
    }
}
