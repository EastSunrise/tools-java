package wsg.tools.internet.info.adult.licence;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.info.adult.common.AdultEntryParser;

/**
 * @author Kingen
 * @see <a href="https://www.chepaishe1.com/">Che Pai She</a>
 * @since 2021/3/9
 */
@ConcreteSite
public class LicencePlateSite extends BaseSite
    implements RepoRetrievable<String, LicencePlateItem> {

    public LicencePlateSite() {
        super("Licence Plate", httpsHost("www.chepaishe1.com"));
    }

    /**
     * Returns the repository of all linked items.
     *
     * @see <a href="https://www.chepaishe1.com/xiyouchepai/">Rare Entries</a>
     */
    public LinkedRepository<String, LicencePlateItem> getRepository() {
        return Repositories.linked(this, "259luxu-959");
    }

    /**
     * Retrieve an item by the specified identifier.
     *
     * @see LicencePlateItem#getId()
     * @see LicencePlateItem#nextId()
     */
    @Nonnull
    @Override
    public LicencePlateItem findById(@Nonnull String id)
        throws NotFoundException, OtherResponseException {
        SnapshotStrategy<Document> strategy = doc -> getNext(doc) == null;
        Document document = getDocument(httpGet("/%s", id.toLowerCase(Locale.ROOT)), strategy);
        Element span = document.selectFirst(".single_info").selectFirst(".date");
        LocalDateTime update = LocalDateTime.parse(span.text().strip(), Constants.YYYY_MM_DD_HH_MM);
        Element article = document.selectFirst(CssSelectors.TAG_ARTICLE);
        String serialNum = article.selectFirst(".entry-title").text();
        Element content = article.selectFirst(".single-content");

        Deque<String> lines = new LinkedList<>();
        Iterator<Element> iterator = content.children().stream().filter(Element::hasText)
            .iterator();
        lines.add(iterator.next().text());
        while (iterator.hasNext()) {
            lines.addAll(DocumentUtils.collectTexts(iterator.next()));
        }
        String desc = lines.removeFirst();
        if (!lines.isEmpty()) {
            desc += lines.removeLast();
        }
        String nextId = getNext(document);
        LicencePlateItem item = new LicencePlateItem(id, serialNum, desc, update, nextId);
        Element img = content.selectFirst(CssSelectors.TAG_IMG);
        if (img != null) {
            item.setCover(NetUtils.createURL(img.attr(CssSelectors.ATTR_SRC)));
        }
        if (lines.isEmpty()) {
            return item;
        }
        Map<String, String> info = AmateurCatSite.extractInfo(lines);
        AdultEntryParser parser = AdultEntryParser.create(info);
        parser.verifySerialNumber(item);
        item.setPerformer(parser.getPerformer());
        item.setDuration(parser.getDuration());
        item.setRelease(parser.getRelease());
        item.setProducer(parser.getProducer());
        item.setDistributor(parser.getDistributor());
        item.setSeries(parser.getSeries());
        item.setTags(parser.getTags(Constants.WHITESPACE));
        parser.check("商品発売日", "対応デバイス", "評価", "简介", "剧情简介");
        return item;
    }

    private String getNext(Document document) {
        Element a = document.selectFirst(".post-next").selectFirst(CssSelectors.TAG_A);
        if (!a.hasAttr(CssSelectors.ATTR_REL)) {
            return null;
        }
        String href = a.attr(CssSelectors.ATTR_HREF);
        String id = RegexUtils.matchesOrElseThrow(Lazy.URL_REGEX, href).group("id");
        return URLDecoder.decode(id, Constants.UTF_8);
    }

    private static class Lazy {

        private static final Pattern URL_REGEX =
            Pattern.compile("https://www\\.chepaishe1\\.com/(?<id>[^/]+)/");
    }
}
