package wsg.tools.internet.resource.movie;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.function.BiThrowableFunction;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.page.PageResult;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.PageRepository;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.RequestBuilder;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
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
    private static final String BT_ATTACH_SRC = "thunder://url_btbtt";
    private static final String BT_ATTACH_PREFIX = "http://51btbtt.com/attach-download";

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
    @Nonnull
    public ListRepository<Integer, GrapeNewsItem> getNewsRepository() {
        Stream<Integer> stream = IntStream.rangeClosed(2, MAX_NEWS_ID).boxed();
        return Repositories.list(this::findNewsItem, stream.collect(Collectors.toList()));
    }

    public PageRepository<GrapeVodIndex, GrapeVodItem> getVodRepository(GrapeVodType type) {
        Objects.requireNonNull(type);
        return Repositories.page(this::findVodItem,
            (BiThrowableFunction<GrapeVodPageRequest, PageResult<GrapeVodIndex>, NotFoundException, OtherResponseException>)
                pageRequest -> findPage(type, pageRequest), GrapeVodPageRequest.first());
    }

    /**
     * Finds an item that belongs to {@link GrapeVodType#BT_MOVIE}.
     */
    @Nonnull
    public GrapeNewsItem findNewsItem(int id) throws NotFoundException, OtherResponseException {
        RequestBuilder builder = builder0("/movie/%d.html", id);
        Document document = getDocument(builder, SnapshotStrategy.never());
        String datetime = document.selectFirst(".updatetime").text();
        LocalDate releaseDate = LocalDate.parse(datetime.substring(5));
        GrapeNewsItem item = new GrapeNewsItem(id, builder.toString(), releaseDate);

        Element h1 = document.selectFirst("div.news_title_all").selectFirst(CssSelectors.TAG_H1);
        item.setTitle(h1.text().strip());
        Element info = document.selectFirst(".text");
        Element image = info.selectFirst(CssSelectors.TAG_IMG);
        if (image != null) {
            item.setCover(NetUtils.createURL(image.attr(CssSelectors.ATTR_SRC)));
        }
        Elements as = info.select(CssSelectors.TAG_A);
        List<AbstractLink> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        for (Element a : as) {
            try {
                String href = a.attr(CssSelectors.ATTR_HREF).strip();
                if (StringUtils.isBlank(href) || Lazy.VOD_REGEX.matcher(href).matches()) {
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
    public GrapeVodPageResult findPage(@Nonnull GrapeVodType type,
        @Nonnull GrapeVodPageRequest pageRequest) throws NotFoundException, OtherResponseException {
        String order = pageRequest.getOrderBy().getText();
        int page = pageRequest.getCurrent() + 1;
        String arg = String.format("vod-type-id-%d-order-%s-p-%d", type.getId(), order, page);
        RequestBuilder builder = builder0("/index.php").addParameter("s", arg);
        Document document = getDocument(builder, SnapshotStrategy.always());
        String summary = ((TextNode) document.selectFirst(".ui-page-big").childNode(0)).text();
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.PAGE_SUM_REGEX, summary.strip());
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

    public GrapeVodItem findVodItem(GrapeVodIndex index)
        throws NotFoundException, OtherResponseException {
        Objects.requireNonNull(index);
        RequestBuilder builder = builder0(index.getPath());
        Document document = getDocument(builder, SnapshotStrategy.never());

        Elements heads = document.selectFirst(".bread-crumbs").select(CssSelectors.TAG_A);
        String typeHref = heads.get(1).attr(CssSelectors.ATTR_HREF);
        String typeText = RegexUtils.matchesOrElseThrow(Lazy.TYPE_HREF_REGEX, typeHref).group("t");
        GrapeVodType type = EnumUtilExt.valueOfText(typeText, GrapeVodType.class, false);
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

    private static class Lazy {

        private static final Pattern VOD_REGEX = Pattern
            .compile("tt\\d+/?|/vod/\\d+/?|/vod/[a-z]+/[a-z]+/?|/html/\\d+\\.html");
        private static final Pattern PAGE_SUM_REGEX = Pattern.compile("共(?<t>\\d+)部 :\\d+/\\d+");
        private static final Pattern TYPE_HREF_REGEX;

        static {
            String types = Arrays.stream(GrapeVodType.values()).map(TextSupplier::getText)
                .collect(Collectors.joining("|"));
            TYPE_HREF_REGEX = Pattern.compile("/vod/list/(?<t>" + types + ")/");
        }
    }
}
