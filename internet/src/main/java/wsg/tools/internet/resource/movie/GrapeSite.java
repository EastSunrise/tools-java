package wsg.tools.internet.resource.movie;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.Repositories;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.IntIndicesRepository;
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

    private static final int MAX_NEWS_ID = 16132;
    private static final Pattern TYPE_HREF_REGEX;
    private static final String BT_ATTACH_SRC = "thunder://url_btbtt";
    private static final String BT_ATTACH_PREFIX = "http://51btbtt.com/attach-download";
    private static final Pattern TIME_REGEX = Pattern.compile("发布时间：(?<s>\\d{4}-\\d{2}-\\d{2})");
    private static final Pattern YEAR_REGEX = Pattern.compile("◎年\\s*代\\s+(?<y>\\d{4})\\s*◎");
    private static final Pattern VOD_REGEX = Pattern
        .compile("tt\\d+/?|/vod/\\d+/?|/vod/[a-z]+/[a-z]+/?|/html/\\d+\\.html");
    private static final Pattern PAGE_SUM_REGEX = Pattern.compile("共(?<t>\\d+)部 :\\d+/\\d+");

    static {
        String types = Arrays.stream(GrapeVodType.values()).map(TextSupplier::getText)
            .collect(Collectors.joining("|"));
        TYPE_HREF_REGEX = Pattern.compile("/vod/list/(?<t>" + types + ")/");
    }

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
     * The repository of the items that belong to {@link GrapeVodType#BT_MOVIE}. <strong>About 1% of
     * the items are not found.</strong>
     */
    public IntIndicesRepository<GrapeNewsItem> getNewsRepository() {
        return Repositories.rangeClosed(this::findNewsItem, 2, MAX_NEWS_ID);
    }

    /**
     * Finds an item that belongs to {@link GrapeVodType#BT_MOVIE}.
     */
    public GrapeNewsItem findNewsItem(int id) throws HttpResponseException {
        RequestBuilder builder = builder0("/movie/%d.html", id);
        Document document = getDocument(builder, SnapshotStrategy.never());
        String datetime = document.selectFirst(".updatetime").text();
        Matcher timeMatcher = RegexUtils.matchesOrElseThrow(TIME_REGEX, datetime);
        LocalDate releaseDate = LocalDate.parse(timeMatcher.group("s"));
        GrapeNewsItem item = new GrapeNewsItem(id, builder.toString(), releaseDate);

        Element h1 = document.selectFirst("div.news_title_all").selectFirst(CssSelectors.TAG_H1);
        item.setTitle(h1.text().strip());
        Element info = document.selectFirst(".text");
        Matcher yearMatcher = YEAR_REGEX.matcher(info.text());
        if (yearMatcher.find()) {
            item.setYear(Integer.parseInt(yearMatcher.group("y")));
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
                if (href.contains(BT_ATTACH_SRC)) {
                    href = href.replace(BT_ATTACH_SRC, BT_ATTACH_PREFIX);
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
        item.setLinks(resources);
        item.setExceptions(exceptions);
        return item;
    }

    /**
     * Finds the page of vod indices by the given type.
     *
     * @see GrapeVodType
     */
    public GrapeVodPageResult findAllVodIndices(@Nonnull GrapeVodType type,
        @Nonnull GrapeVodPageRequest pageRequest) throws HttpResponseException {
        String order = pageRequest.getOrderBy().getText();
        int page = pageRequest.getCurrent() + 1;
        String arg = String.format("vod-type-id-%d-order-%s-p-%d", type.getId(), order, page);
        RequestBuilder builder = builder0("/index.php").addParameter("s", arg);
        Document document = getDocument(builder, SnapshotStrategy.always());

        String summary = ((TextNode) document.selectFirst(".ui-page-big").childNode(0)).text();
        Matcher matcher = RegexUtils.matchesOrElseThrow(PAGE_SUM_REGEX, summary.strip());
        int total = Integer.parseInt(matcher.group("t"));

        Elements lis = document.selectFirst("#contents").select(CssSelectors.TAG_LI);
        List<GrapeVodIndex> indices = new ArrayList<>();
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_H5).selectFirst(CssSelectors.TAG_A);
            String path = a.attr(CssSelectors.ATTR_HREF);
            String title = a.text();
            Node node = li.selectFirst(".long").nextSibling();
            LocalDate updateTime = LocalDate.parse(((TextNode) node).text().strip());
            String state = li.selectFirst(".mod_version").text();
            indices.add(new GrapeVodIndex(path, title, updateTime, state));
        }
        return new GrapeVodPageResult(indices, pageRequest, total);
    }

    public GrapeVodItem findVodItem(@Nonnull GrapeVodIndex index) throws HttpResponseException {
        RequestBuilder builder = builder0(index.getPath());
        Document document = getDocument(builder, SnapshotStrategy.never());

        Elements heads = document.selectFirst(".bread-crumbs").select(CssSelectors.TAG_A);
        String typeHref = heads.get(1).attr(CssSelectors.ATTR_HREF);
        String typeText = RegexUtils.matchesOrElseThrow(TYPE_HREF_REGEX, typeHref).group("t");
        GrapeVodType type = EnumUtilExt.deserializeText(typeText, GrapeVodType.class, false);
        String timeStr = document.selectFirst("#addtime").text().strip();
        LocalDateTime addTime = LocalDateTime.parse(timeStr, Constants.YYYY_MM_DD_HH_MM);
        GrapeVodItem item = new GrapeVodItem(index.getPath(), builder.toString(), type, addTime);

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
        item.setLinks(resources);
        item.setExceptions(exceptions);
        return item;
    }
}
