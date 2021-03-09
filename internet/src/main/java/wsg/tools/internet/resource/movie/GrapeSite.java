package wsg.tools.internet.resource.movie;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.IntRangeIdentifiedRepositoryImpl;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.IntRangeIdentifiedRepository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.LinkFactory;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.impl.Ed2kLink;

/**
 * @author Kingen
 * @see <a href="https://www.putaoys.com">Grape Video</a>
 * @since 2021/3/2
 */
public final class GrapeSite extends BaseSite {

    public static final int MAX_NEWS_ID = 16132;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm");
    private static final Pattern TIME_REGEX = Pattern.compile("发布时间：(?<s>\\d{4}-\\d{2}-\\d{2})");
    private static final Pattern YEAR_REGEX = Pattern.compile("◎年\\s*代\\s+(?<y>\\d{4})\\s*◎");
    private static final Pattern VOD_REGEX = Pattern
        .compile("tt\\d+/?|/vod/\\d+/?|/vod/[a-z]+/[a-z]+/?|/html/\\d+\\.html");
    private static final Pattern VOD_GENRE_HREF_REGEX = Pattern.compile("/vod/list/(?<t>[a-z]+)");
    private static final Pattern PAGE_SUM_REGEX = Pattern.compile("共(?<t>\\d+)部 :\\d+/\\d+");
    private static final String BT_ATTACH = "url_btbtt";

    public GrapeSite() {
        super("Grape Vod", new BasicHttpSession("putaoys.com"), GrapeSite::handleResponse);
    }

    private static String handleResponse(HttpResponse response) throws IOException {
        try {
            return DEFAULT_RESPONSE_HANDLER.handleResponse(response);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
                throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, e.getReasonPhrase());
            }
            throw e;
        }
    }

    /**
     * The repository including downloadable resources.
     */
    public IntRangeIdentifiedRepository<GrapeNewsItem> getNewsRepository() {
        return new IntRangeIdentifiedRepositoryImpl<>(this::findNewsItem, MAX_NEWS_ID);
    }

    public GrapeNewsItem findNewsItem(@Nonnull Integer id) throws HttpResponseException {
        RequestBuilder builder = builder0("/movie/%d.html", id);
        Document document = getDocument(builder, SnapshotStrategy.never());
        LocalDate releaseDate = LocalDate.parse(
            RegexUtils.matchesOrElseThrow(TIME_REGEX, document.selectFirst(".updatetime").text())
                .group("s"));
        GrapeNewsItem item = new GrapeNewsItem(id, builder.toString(), releaseDate);

        item.setTitle(
            document.selectFirst("div.news_title_all").selectFirst(CssSelectors.TAG_H1).text()
                .strip());
        Element info = document.selectFirst(".text");
        Matcher matcher = YEAR_REGEX.matcher(info.text());
        if (matcher.find()) {
            item.setYear(Integer.parseInt(matcher.group("y")));
        }

        Elements as = info.select(CssSelectors.TAG_A);
        List<AbstractLink> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        for (Element a : as) {
            try {
                String href = a.attr(CssSelectors.ATTR_HREF).strip();
                if (StringUtils.isBlank(href) || VOD_REGEX.matcher(href).matches()) {
                    continue;
                }
                if (href.contains(BT_ATTACH)) {
                    href = href
                        .replace("thunder://url_btbtt", "http://51btbtt.com/attach-download");
                }
                int index = StringUtils.indexOf(href, Ed2kLink.ED2K_PREFIX);
                if (StringUtils.INDEX_NOT_FOUND != index) {
                    href = href.substring(index);
                }
                resources.add(LinkFactory.create(a.text().strip(), href, () -> {
                    Node node = a.nextSibling();
                    if (node instanceof TextNode) {
                        return LinkFactory.getPassword(((TextNode) node).text());
                    }
                    return null;
                }));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);
        return item;
    }

    /**
     * Obtains the page of simple vod items by the given type.
     *
     * @return the page of the simple items
     */
    public GrapeVodPageResult findAllVodSimples(@Nonnull GrapeVodType type,
        @Nonnull GrapeVodPageRequest pageRequest) throws HttpResponseException {
        String arg = String.format("vod-type-id-%d-order-%s-p-%d", type.getCode(),
            pageRequest.getOrderBy().getText(), pageRequest.getCurrent() + 1);
        RequestBuilder builder = builder0("/index.php").addParameter("s", arg);
        Document document = getDocument(builder, SnapshotStrategy.always());

        String summary = ((TextNode) document.selectFirst(".ui-page-big").childNode(0)).text();
        Matcher matcher = RegexUtils.matchesOrElseThrow(PAGE_SUM_REGEX, summary.strip());
        int total = Integer.parseInt(matcher.group("t"));

        Elements lis = document.selectFirst("#contents").select(CssSelectors.TAG_LI);
        List<GrapeVodSimpleItem> items = new ArrayList<>();
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_H5).selectFirst(CssSelectors.TAG_A);
            String path = a.attr(CssSelectors.ATTR_HREF);
            String title = a.text();
            Node node = li.selectFirst(".long").nextSibling();
            LocalDate updateTime = LocalDate.parse(((TextNode) node).text().strip());
            String state = li.selectFirst(".mod_version").text();
            items.add(new GrapeVodSimpleItem(path, title, updateTime, state));
        }
        return new GrapeVodPageResult(items, pageRequest, total);
    }

    public GrapeVodItem findVodItem(@Nonnull String path) throws HttpResponseException {
        RequestBuilder builder = builder0(path);
        Document document = getDocument(builder, SnapshotStrategy.never());

        Elements heads = document.selectFirst("ul.bread-crumbs").select(CssSelectors.TAG_A);
        AssertUtils.requireEquals(heads.size(), 3);
        String genreHref = heads.get(1).attr(CssSelectors.ATTR_HREF);
        Matcher matcher = RegexUtils.matchesOrElseThrow(VOD_GENRE_HREF_REGEX, genreHref);
        GrapeVodGenre genre = EnumUtilExt
            .deserializeText(matcher.group("t"), GrapeVodGenre.class, false);
        LocalDateTime addTime = LocalDateTime
            .parse(document.selectFirst("#addtime").text().strip(), FORMATTER);
        GrapeVodItem item = new GrapeVodItem(path, builder.toString(), genre, addTime);

        Element div = document.selectFirst(".detail-title");
        Element h1 = div.selectFirst(CssSelectors.TAG_H);
        item.setTitle(h1.text().strip());
        Element span = h1.nextElementSibling();
        if (span.is(CssSelectors.TAG_SPAN)) {
            int year = Integer.parseInt(StringUtils.strip(span.text(), "()"));
            item.setYear(year);
        }
        Map<String, Element> info = document.selectFirst(".info").select("dl").stream()
            .collect(Collectors.toMap(Element::text, e -> e));
        Element dl = info.get("状态：");
        if (dl != null) {
            item.setState(dl.nextElementSibling().text().strip());
        }

        Elements lis = document.select("#downul").select(CssSelectors.TAG_LI);
        List<AbstractLink> resources = new ArrayList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        for (Element li : lis) {
            Element input = li.selectFirst(CssSelectors.TAG_INPUT);
            try {
                resources.add(LinkFactory.create(input.attr("file_name"), input.val()));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);
        return item;
    }
}
