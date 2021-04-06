package wsg.tools.internet.info.adult.licence;

import java.net.URL;
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
import org.jetbrains.annotations.Contract;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UnexpectedContentException;
import wsg.tools.internet.info.adult.AdultEntryBuilder;
import wsg.tools.internet.info.adult.AmateurAdultEntry;
import wsg.tools.internet.info.adult.BasicAdultEntryBuilder;

/**
 * The site is suspected as a partial copy of {@link LicencePlateSite}.
 *
 * @author Kingen
 * @see <a href="http://www.surenmao.com/">Amateur Cat</a>
 * @since 2021/2/28
 */
@ConcreteSite
public final class AmateurCatSite extends BaseSite
    implements RepoRetrievable<String, AmateurCatItem> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    private static final String FIRST_KEY = "収録時間";

    public AmateurCatSite() {
        super("Amateur Cat", httpHost("surenmao.com"));
    }

    /**
     * Extracts information from the given lines.
     *
     * @see LicencePlateSite
     */
    @Nonnull
    static Map<String, String> extractInfo(@Nonnull Collection<String> lines) {
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
     * Returns the repository of all linked items.
     *
     * @see <a href="http://www.surenmao.com/200gana-1829">Get Started</a>
     */
    @Nonnull
    @Contract(" -> new")
    public LinkedRepository<String, AmateurCatItem> getRepository() {
        return Repositories.linked(this, "200gana-1829");
    }

    @Nonnull
    @Override
    public AmateurCatItem findById(@Nonnull String id)
        throws NotFoundException, OtherResponseException {
        Document document = getDocument(httpGet("/%s", id), doc -> getNext(doc) == null);
        AmateurCatItem item = new AmateurCatItem(id);

        Element main = document.selectFirst("#main");
        item.setAuthor(main.selectFirst("span.author").text());
        String published = main.selectFirst("time.published").attr(CssSelectors.ATTR_DATETIME);
        item.setPublished(LocalDateTime.parse(published, FORMATTER));
        String updated = main.selectFirst("time.updated").attr(CssSelectors.ATTR_DATETIME);
        item.setUpdated(LocalDateTime.parse(updated, FORMATTER));
        item.setNext(getNext(document));

        String code = main.selectFirst("h1.entry-title").text();
        String src = main.selectFirst("img.size-full").attr(CssSelectors.ATTR_SRC);
        URL cover = NetUtils.createURL(src);
        Element content = main.selectFirst("div.entry-content");
        Pair<String, List<String>> pair = getLinesAndDesc(content);
        BasicAdultEntryBuilder builder;
        List<String> lines = pair.getRight();
        if (lines == null) {
            builder = BasicAdultEntryBuilder.builder(code);
        } else {
            Map<String, String> info = extractInfo(lines);
            builder = AdultEntryBuilder.builder(code, info)
                .duration().release().producer().distributor().series()
                .validateCode().tags(Constants.WHITESPACE);
        }
        AmateurAdultEntry entry = builder.description(pair.getLeft())
            .images(Collections.singletonList(cover)).amateur();
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
        return RegexUtils.matchesOrElseThrow(Lazy.HREF_REGEX, href).group("id");
    }

    private static class Lazy {

        private static final Pattern HREF_REGEX = Pattern
            .compile("http://www\\.surenmao\\.com/(?<id>[0-9a-z-]+)/");
    }
}
