package wsg.tools.internet.info.adult;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.Repositories;
import wsg.tools.internet.base.impl.WithoutNextDocument;
import wsg.tools.internet.base.intf.LinkedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.info.adult.common.AdultEntry;
import wsg.tools.internet.info.adult.common.AdultEntryBuilder;

/**
 * @author Kingen
 * @see <a href="https://www.chepaishe1.com/">Che Pai She</a>
 * @since 2021/3/9
 */
public class LicencePlateSite extends BaseSite implements Repository<String, LicencePlateItem> {

    private static final Pattern URL_REGEX =
        Pattern.compile("https://www\\.chepaishe1\\.com/(?<id>[^/]+)/");

    public LicencePlateSite() {
        super("Licence Plate", new BasicHttpSession("chepaishe1.com"));
    }

    /**
     * Returns the repository of all linked items.
     *
     * @see <a href="https://www.chepaishe1.com/xiyouchepai/">Rare Entries</a>
     */
    public LinkedRepository<String, LicencePlateItem> getRepository() {
        return Repositories.linked(this, "259luxu-959");
    }

    @Override
    public LicencePlateItem findById(@Nonnull String id) throws HttpResponseException {
        SnapshotStrategy<Document> strategy = new WithoutNextDocument<>(this::getNext);
        Document document = getDocument(builder0("/%s", id), strategy);
        Element span = document.selectFirst(".single_info").selectFirst(".date");
        LocalDateTime update = LocalDateTime.parse(span.text().strip(), Constants.YYYY_MM_DD_HH_MM);
        Element article = document.selectFirst(CssSelectors.TAG_ARTICLE);
        String code = article.selectFirst(".entry-title").text();
        Element content = article.selectFirst(".single-content");
        List<String> images = content.select(CssSelectors.TAG_IMG).eachAttr(CssSelectors.ATTR_SRC);

        Deque<String> lines = new LinkedList<>();
        Iterator<Element> it = content.children().stream().filter(Element::hasText).iterator();
        lines.add(it.next().text());
        while (it.hasNext()) {
            lines.addAll(DocumentUtils.collectTexts(it.next()));
        }
        String description = lines.removeFirst();
        if (!lines.isEmpty()) {
            description += lines.removeLast();
        }
        if (lines.isEmpty()) {
            AdultEntry entry = AdultEntryBuilder.basic(code)
                .description(description).images(images).build();
            return new LicencePlateItem(id, update, entry, getNext(document));
        }
        Map<String, String> info = LaymanCatSite.extractInfo(lines);
        info.remove("対応デバイス");
        info.remove("評価");
        AdultEntry entry = AdultEntryBuilder.layman(info, code)
            .duration().release().producer().distributor().series().tags(Constants.WHITESPACE)
            .validateCode().description(description).images(images).build();
        return new LicencePlateItem(id, update, entry, getNext(document));
    }

    private String getNext(Document document) {
        Element a = document.selectFirst(".post-next").selectFirst(CssSelectors.TAG_A);
        if (!a.hasAttr(CssSelectors.ATTR_REL)) {
            return null;
        }
        String href = a.attr(CssSelectors.ATTR_HREF);
        String id = RegexUtils.matchesOrElseThrow(URL_REGEX, href).group("id");
        return URLDecoder.decode(id, Constants.UTF_8);
    }
}
