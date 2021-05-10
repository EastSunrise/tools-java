package wsg.tools.internet.info.adult.midnight;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.RequestBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.function.TriFunction;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.repository.RepoPageable;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.info.adult.common.AdultEntryParser;

/**
 * @author Kingen
 * @see <a href="https://www.shenyequ.com/">Midnight Zone</a>
 * @since 2021/2/22
 */
@ConcreteSite
public final class MidnightSite extends BaseSite
    implements RepoPageable<MidnightPageReq, MidnightPageResult> {

    private static final String NAV_NAVIGATION = "nav.navigation";

    public MidnightSite() {
        super("Midnight", httpsHost("www.shenyequ.com"));
    }

    /**
     * Retrieves the paged result of indices under the given column.
     */
    @Nonnull
    @Override
    @Contract("_ -> new")
    public MidnightPageResult findPage(@Nonnull MidnightPageReq req)
        throws NotFoundException, OtherResponseException {
        String page = req.getCurrent() == 0 ? "" : "_" + (req.getCurrent() + 1);
        RequestBuilder builder = httpGet("/%s/index%s.html", req.getColumn().getAsPath(), page);
        Document document = getDocument(builder);
        List<MidnightIndex> indices = new ArrayList<>();
        Element article = document.selectFirst(".article");
        Elements lis = article.select(CssSelectors.TAG_LI);
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_A);
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.ITEM_URL_REGEX, href);
            int id = Integer.parseInt(matcher.group("id"));
            String title = a.attr(CssSelectors.ATTR_TITLE);
            indices.add(new MidnightIndex(id, title));
        }
        int total = Integer.parseInt(article.selectFirst("#max-page").text());
        return new MidnightPageResult(indices, req, total);
    }

    /**
     * Retrieves an item with a collection of adult entries.
     *
     * @see MidnightIndex#getId()
     */
    @Nonnull
    public MidnightCollection findCollection(int id)
        throws NotFoundException, OtherResponseException {
        return getItem(MidnightColumn.COLLECTION, id, (title, addTime, contents) -> {
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
            return new MidnightCollection(id, title, addTime, works);
        });
    }

    /**
     * Retrieves an item with images.
     *
     * @see MidnightIndex#getId()
     */
    @Nonnull
    public MidnightAlbum findAlbum(int id) throws NotFoundException, OtherResponseException {
        return getItem(MidnightColumn.ALBUM, id,
            (title, addTime, contents) -> new MidnightAlbum(id, title, addTime,
                getImages(contents)));
    }

    /**
     * Retrieves an item with an amateur adult entry.
     *
     * @see MidnightColumn#isAmateur()
     * @see MidnightIndex#getId()
     */
    public MidnightAmateurEntry findAmateurEntry(@Nonnull MidnightColumn column, int id)
        throws NotFoundException, OtherResponseException {
        if (!column.isAmateur()) {
            throw new IllegalArgumentException("Not an amateur column");
        }
        return getItem(column, id, (title, addTime, contents) -> {
            List<URL> images = getImages(contents);
            MidnightAmateurEntry entry = new MidnightAmateurEntry(id, title, addTime, images);
            AdultEntryParser parser = AdultEntryParser.create(getInfo(contents));
            entry.setSerialNum(parser.getSerialNum());
            entry.setPerformer(parser.getPerformer());
            entry.setDuration(parser.getDuration());
            entry.setPublish(parser.getPublish());
            entry.setProducer(StringUtilsExt.convertFullWidth(parser.getProducer()));
            entry.setDistributor(StringUtilsExt.convertFullWidth(parser.getDistributor()));
            entry.setSeries(StringUtilsExt.convertFullWidth(parser.getSeries()));
            return entry;
        });
    }

    /**
     * Retrieves an item with a formal adult entry.
     *
     * @see MidnightIndex#getId()
     */
    @Nonnull
    public MidnightFormalEntry findFormalEntry(int id)
        throws NotFoundException, OtherResponseException {
        return getItem(MidnightColumn.ENTRY, id, (title, addTime, contents) -> {
            List<URL> images = getImages(contents);
            MidnightFormalEntry entry = new MidnightFormalEntry(id, title, addTime, images);
            AdultEntryParser parser = AdultEntryParser.create(getInfo(contents));
            entry.setSerialNum(parser.getSerialNum());
            entry.setActresses(parser.getActresses(", "));
            entry.setDuration(parser.getDuration());
            entry.setPublish(parser.getPublish());
            entry.setProducer(StringUtilsExt.convertFullWidth(parser.getProducer()));
            entry.setDistributor(StringUtilsExt.convertFullWidth(parser.getDistributor()));
            entry.setSeries(StringUtilsExt.convertFullWidth(parser.getSeries()));
            return entry;
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
        RequestBuilder builder = httpGet("/%s/%d.html", column.getAsPath(), id);
        Document document = getDocument(builder);
        String datetime = document.selectFirst("time.data-time").text();
        LocalDateTime addTime = LocalDateTime.parse(datetime, Constants.YYYY_MM_DD_HH_MM_SS);
        String title = document.selectFirst("h1.title").text();
        T t = constructor.apply(title, addTime, getContents(document));
        String keywords = DocumentUtils.getMetadata(document).get("keywords");
        if (StringUtils.isNotBlank(keywords)) {
            t.setKeywords(keywords.split(","));
        }
        Element navigation = document.selectFirst(".post-navigation");
        String preHref = navigation.selectFirst(".pre-post").attr(CssSelectors.ATTR_HREF);
        if (!"#".equals(preHref)) {
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.ITEM_URL_REGEX, preHref);
            t.setPreviousId(Integer.parseInt(matcher.group("id")));
        }
        String nextHref = navigation.selectFirst(".next-post").attr(CssSelectors.ATTR_HREF);
        if (!"#".equals(nextHref)) {
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.ITEM_URL_REGEX, nextHref);
            t.setNextId(Integer.parseInt(matcher.group("id")));
        }
        return t;
    }

    @Nonnull
    private List<Element> getContents(@Nonnull Document document)
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
            document = getDocument(httpGet(URI.create(nextHref).getPath()));
        }
        return contents;
    }

    /**
     * Obtains a map of information from the contents.
     */
    @Nonnull
    private Map<String, String> getInfo(@Nonnull List<Element> contents) {
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
            MapUtilsExt.putIfAbsent(info, key, parts[1].strip());
        }
        return info;
    }

    private @Nullable List<URL> getImages(@Nonnull List<Element> contents) {
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
                            href = getHost().toURI() + href;
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
            String columns = Arrays.stream(MidnightColumn.values()).map(PathSupplier::getAsPath)
                .collect(Collectors.joining("|"));
            ITEM_URL_REGEX = Pattern.compile(
                "https://www\\.shenyequ\\.com/(?<t>" + columns + ")/(?<id>\\d+).html");
        }
    }
}
