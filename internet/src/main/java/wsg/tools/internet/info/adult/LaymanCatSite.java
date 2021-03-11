package wsg.tools.internet.info.adult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
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
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.MapUtilsExt;
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
import wsg.tools.internet.info.adult.common.AdultEntry;
import wsg.tools.internet.info.adult.common.AdultEntryBuilder;

/**
 * The site is suspected as a partial copy of {@link LicencePlateSite}.
 *
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

    /**
     * Extracts information from the given lines.
     *
     * @see wsg.tools.internet.info.adult.LicencePlateSite
     */
    static Map<String, String> extractInfo(Collection<String> lines) {
        Iterator<String[]> iterator = lines.stream()
            .map(s -> StringUtils.stripStart(s, "・"))
            .map(s -> s.split("：", 2))
            .iterator();
        String[] first = iterator.next();
        if (FIRST_KEY.equals(first[0])) {
            first[1] = first[1].split("・")[0];
        }
        Map<String, String> info = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        info.put(first[0], first[1].strip());
        while (iterator.hasNext()) {
            String[] parts = iterator.next();
            if (parts.length == 1) {
                continue;
            }
            MapUtilsExt.putIfAbsent(info, parts[0], parts[1].strip());
        }
        return info;
    }

    /**
     * @see <a href="http://www.surenmao.com/200gana-1829">Get Started</a>
     */
    public LinkedRepository<String, LaymanCatItem> getRepository() {
        return new LinkedRepositoryImpl<>(this, "200gana-1829");
    }

    @Override
    public LaymanCatItem findById(@Nonnull String id) throws HttpResponseException {
        WithoutNextDocument<String> strategy = new WithoutNextDocument<>(this::getNext);
        Document document = getDocument(builder0("/%s", id), strategy);
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
        Pair<String, List<String>> pair = getLinesAndDesc(content);
        AdultEntryBuilder builder;
        List<String> lines = pair.getRight();
        if (lines == null) {
            builder = AdultEntryBuilder.basic(code).images(Collections.singletonList(cover));
        } else {
            Map<String, String> info = extractInfo(lines);
            builder = AdultEntryBuilder.layman(info, code)
                .duration().release().producer().distributor().series()
                .validateCode().tags(Constants.WHITESPACE).images(Collections.singletonList(cover));
        }
        AdultEntry entry = builder.description(pair.getLeft()).build();
        item.setEntry(entry);
        return item;
    }

    /**
     * @return description and lines of information, may null but at least one is not null
     */
    private Pair<String, List<String>> getLinesAndDesc(Element content) {
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
                return Pair.of(description.toString(), null);
            }
            while (current.childNodeSize() == 1) {
                description.append(current.text());
                current = current.nextElementSibling();
            }
            List<String> lines = DocumentUtils.collectTexts(current);
            return Pair.of(description.toString(), lines);
        }
        List<Element> divs = map.get(CssSelectors.TAG_DIV);
        if (divs != null) {
            List<String> lines = divs.stream().map(Element::text)
                .map(String::strip).collect(Collectors.toList());
            return Pair.of(children.get(1).text(), lines);
        }
        List<Element> article = map.get(CssSelectors.TAG_ARTICLE);
        if (article != null) {
            List<String> lines = DocumentUtils.collectTexts(article.get(0));
            Iterator<String> iterator = lines.iterator();
            String description = iterator.next() + iterator.next() + iterator.next();
            return Pair.of(description, lines.subList(3, lines.size()));
        }
        throw new UnexpectedContentException("Unknown content");
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