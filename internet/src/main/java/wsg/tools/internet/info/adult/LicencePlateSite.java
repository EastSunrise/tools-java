package wsg.tools.internet.info.adult;

import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.SnapshotStrategies;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.info.adult.entry.AdultEntryBuilder;
import wsg.tools.internet.info.adult.entry.AmateurAdultEntry;
import wsg.tools.internet.info.adult.entry.BasicAdultEntryBuilder;

/**
 * @author Kingen
 * @see <a href="https://www.chepaishe1.com/">Che Pai She</a>
 * @since 2021/3/9
 */
@ConcreteSite
public class LicencePlateSite extends BaseSite
    implements RepoRetrievable<String, LicencePlateItem> {

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

    @Nonnull
    @Override
    public LicencePlateItem findById(String id) throws NotFoundException, OtherResponseException {
        Objects.requireNonNull(id);
        SnapshotStrategy<Document> strategy = SnapshotStrategies.withoutNext(this::getNext);
        Document document = getDocument(builder0("/%s", id.toLowerCase(Locale.ROOT)), strategy);
        Element span = document.selectFirst(".single_info").selectFirst(".date");
        LocalDateTime update = LocalDateTime.parse(span.text().strip(), Constants.YYYY_MM_DD_HH_MM);
        Element article = document.selectFirst(CssSelectors.TAG_ARTICLE);
        String code = article.selectFirst(".entry-title").text();
        Element content = article.selectFirst(".single-content");
        List<URL> images = content.select(CssSelectors.TAG_IMG).eachAttr(CssSelectors.ATTR_SRC)
            .stream().map(NetUtils::createURL).collect(Collectors.toList());

        Deque<String> lines = new LinkedList<>();
        Iterator<Element> iterator = content.children().stream().filter(Element::hasText)
            .iterator();
        lines.add(iterator.next().text());
        while (iterator.hasNext()) {
            lines.addAll(DocumentUtils.collectTexts(iterator.next()));
        }
        String description = lines.removeFirst();
        if (!lines.isEmpty()) {
            description += lines.removeLast();
        }
        if (lines.isEmpty()) {
            AmateurAdultEntry entry = BasicAdultEntryBuilder.builder(code)
                .description(description).images(images).amateur();
            return new LicencePlateItem(id, update, entry, getNext(document));
        }
        Map<String, String> info = AmateurCatSite.extractInfo(lines);
        String intro = MapUtilsExt.getString(info, "简介", "剧情简介");
        AmateurAdultEntry entry = AdultEntryBuilder.builder(code, info)
            .duration().release().producer().distributor().series().tags(Constants.WHITESPACE)
            .validateCode().description(description).images(images)
            .ignore("商品発売日", "対応デバイス", "評価").amateur();
        LicencePlateItem item = new LicencePlateItem(id, update, entry, getNext(document));
        item.setIntro(intro);
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
