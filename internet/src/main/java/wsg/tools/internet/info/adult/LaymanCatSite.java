package wsg.tools.internet.info.adult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.LinkedRepositoryImpl;
import wsg.tools.internet.base.impl.WithoutNextDocument;
import wsg.tools.internet.base.intf.LinkedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.common.Scheme;
import wsg.tools.internet.common.UnexpectedContentException;

/**
 * @author Kingen
 * @see <a href="http://www.surenmao.com/">Layman Cat</a>
 * @since 2021/2/28
 */
public final class LaymanCatSite extends BaseSite implements Repository<String, LaymanCatItem> {

    private static final Pattern HREF_REGEX = Pattern
        .compile("http://www\\.surenmao\\.com/(?<id>[0-9a-z-]+)/");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    private static final String FIRST_KEY = "収録時間";

    public LaymanCatSite() {
        super("Layman Cat", new BasicHttpSession(Scheme.HTTP, "surenmao.com"));
    }

    public LinkedRepository<String, LaymanCatItem> getRepository() {
        return new LinkedRepositoryImpl<>(this, "200gana-1829");
    }

    @Override
    public LaymanCatItem findById(@Nonnull String id) throws HttpResponseException {
        WithoutNextDocument<String> strategy = new WithoutNextDocument<>(this::getNext);
        Document document = getDocument(builder0("/%s/", id), strategy);
        LaymanCatItem item = new LaymanCatItem(id);

        Element main = document.selectFirst("#main");
        item.setAuthor(main.selectFirst("span.author").text());
        String published = main.selectFirst("time.published").attr(CssSelectors.ATTR_DATETIME);
        item.setPublished(LocalDateTime.parse(published, FORMATTER));
        String updated = main.selectFirst("time.updated").attr(CssSelectors.ATTR_DATETIME);
        item.setUpdated(LocalDateTime.parse(updated, FORMATTER));
        item.setNext(getNext(document));

        String code = main.selectFirst("h1.entry-title").text();
        String cover = main.selectFirst("img.size-full").attr(CssSelectors.ATTR_SRC);
        Element content = main.selectFirst("div.entry-content");
        Pair<String, AdultEntry> pair = getDescAndEntry(content, code, cover);
        item.setDescription(pair.getLeft());
        item.setEntry(pair.getRight());
        return item;
    }

    private Pair<String, AdultEntry> getDescAndEntry(Element content, String code, String cover) {
        Elements children = content.children();
        Map<String, List<Element>> map = children.stream()
            .collect(Collectors.groupingBy(Element::tagName));
        if (map.size() == 1) {
            Element current = children.first();
            if (current.childNodeSize() == 1) {
                current = current.nextElementSibling();
            }
            StringBuilder description = new StringBuilder(current.text());
            current = current.nextElementSibling();
            if (current == null) {
                return Pair.of(description.toString(), new AdultEntry(code, cover));
            }
            while (current.childNodeSize() == 1) {
                description.append(current.text());
                current = current.nextElementSibling();
            }
            List<String> lines = current.textNodes().stream()
                .map(TextNode::text)
                .collect(Collectors.toList());
            return Pair.of(description.toString(), getEntry(code, cover, lines));
        }
        if (map.containsKey(CssSelectors.TAG_DIV)) {
            List<String> lines = map.get(CssSelectors.TAG_DIV).stream()
                .map(Element::text)
                .collect(Collectors.toList());
            return Pair.of(children.get(1).text(), getEntry(code, cover, lines));
        }
        List<Element> article = map.get("article");
        if (article != null) {
            List<String> lines = DocumentUtils.collectTexts(article.get(0));
            Iterator<String> iterator = lines.iterator();
            String description = iterator.next() + iterator.next() + iterator.next();
            AdultEntry entry = getEntry(code, cover, lines.subList(3, lines.size()));
            return Pair.of(description, entry);
        }
        throw new UnexpectedContentException("Unknown content");
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

    private String getNext(Document document) {
        Element next = document.selectFirst("div.nav-next");
        if (next == null) {
            return null;
        }
        String href = next.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
        return RegexUtils.matchesOrElseThrow(HREF_REGEX, href).group("id");
    }
}
