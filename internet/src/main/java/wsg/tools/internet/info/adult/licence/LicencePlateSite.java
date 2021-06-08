package wsg.tools.internet.info.adult.licence;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.Constants;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.SiteStatus;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.BasicSiblingEntity;
import wsg.tools.internet.base.view.SiblingSupplier;
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
@ConcreteSite(status = SiteStatus.INVALID)
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
     * Retrieve an item by the specified path.
     *
     * @see LicencePlateItem#getAsPath()
     * @see LicencePlateItem#getNextId()
     * @see LicencePlateItem#getPreviousId()
     */
    @Nonnull
    @Override
    public LicencePlateItem findById(@Nonnull String path)
        throws NotFoundException, OtherResponseException {
        Document document = this.getDocument(this.httpGet("/%s", path));
        Element span = document.selectFirst(".single_info").selectFirst(".date");
        LocalDateTime update = LocalDateTime.parse(span.text().strip(), Constants.YYYY_MM_DD_HH_MM);
        Element article = document.selectFirst(CssSelectors.TAG_ARTICLE);
        int id = Integer.parseInt(article.id().substring(5));
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
        SiblingSupplier<String> sibling = this.getSibling(document);
        LicencePlateItem item = new LicencePlateItem(id, serialNum, desc, update,
            sibling.getPreviousId(), sibling.getNextId());
        Element img = content.selectFirst(CssSelectors.TAG_IMG);
        if (img != null) {
            item.setImage(NetUtils.createURL(img.attr(CssSelectors.ATTR_SRC)));
        }
        if (lines.isEmpty()) {
            return item;
        }
        Map<String, String> info = AmateurCatSite.extractInfo(lines);
        AdultEntryParser parser = AdultEntryParser.create(info);
        parser.verifySerialNumber(item);
        item.setPerformer(parser.getPerformer());
        item.setDuration(parser.getDuration());
        item.setPublish(parser.getPublish());
        item.setProducer(parser.getProducer());
        item.setDistributor(parser.getDistributor());
        item.setSeries(parser.getSeries());
        item.setTags(parser.getTags(Constants.WHITESPACE));
        parser.check("商品発売日", "対応デバイス", "評価", "简介", "剧情简介");
        return item;
    }

    private SiblingSupplier<String> getSibling(Document document) {
        Elements navigation = document.selectFirst(".post-navigation").select(CssSelectors.TAG_A);
        String previous = null;
        Element first = navigation.first();
        if (first.hasAttr(CssSelectors.ATTR_REL)) {
            String href = first.attr(CssSelectors.ATTR_HREF);
            previous = RegexUtils.matchesOrElseThrow(Lazy.URL_REGEX, href).group("id");
        }
        String next = null;
        Element last = navigation.last();
        if (last.hasAttr(CssSelectors.ATTR_REL)) {
            String href = last.attr(CssSelectors.ATTR_HREF);
            next = RegexUtils.matchesOrElseThrow(Lazy.URL_REGEX, href).group("id");
        }
        return new BasicSiblingEntity<>(previous, next);
    }

    private static class Lazy {

        private static final Pattern URL_REGEX =
            Pattern.compile("https://www\\.chepaishe1\\.com/(?<id>[^/]+)/");
    }
}
