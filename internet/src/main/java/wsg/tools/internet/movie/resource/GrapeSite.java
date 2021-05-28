package wsg.tools.internet.movie.resource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.RequestBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.page.AmountCountablePage;
import wsg.tools.internet.base.page.Page;
import wsg.tools.internet.base.page.PageIndex;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.support.Ed2kLink;
import wsg.tools.internet.download.support.InvalidResourceException;
import wsg.tools.internet.download.support.LinkFactory;

/**
 * @author Kingen
 * @see <a href="https://www.putaoys.com">Grape Video</a>
 * @since 2021/3/2
 */
@ConcreteSite
public final class GrapeSite extends AbstractListResourceSite<GrapeNewsItem> {

    private static final int MAX_NEWS_ID = 16132;
    private static final String BT_ATTACH_SRC = "thunder://url_btbtt";
    private static final String BT_ATTACH_PREFIX = "http://51btbtt.com/attach-download";

    public GrapeSite() {
        super("Grape Vod", httpsHost("putaoys.com"));
    }

    /**
     * The repository of the items that belong to {@link GrapeVodType#BT_MOVIE}. <strong>About 1% of
     * the items are not found.</strong>
     */
    @Override
    @Nonnull
    public ListRepository<Integer, GrapeNewsItem> getRepository() {
        Stream<Integer> stream = IntStream.rangeClosed(2, MAX_NEWS_ID).boxed();
        return Repositories.list(this, stream.collect(Collectors.toList()));
    }

    /**
     * Retrieves an item that belongs to {@link GrapeVodType#BT_MOVIE}.
     */
    @Override
    @Nonnull
    public GrapeNewsItem findById(@Nonnull Integer id)
        throws NotFoundException, OtherResponseException {
        Document document = getDocument(httpGet("/movie/%d.html", id));
        String datetime = document.selectFirst(".updatetime").text();
        LocalDate releaseDate = LocalDate.parse(datetime.substring(5));
        Element h1 = document.selectFirst("div.news_title_all").selectFirst(CssSelectors.TAG_H1);
        String title = h1.text().strip();
        GrapeNewsItem item = new GrapeNewsItem(id, title, releaseDate);

        Element info = document.selectFirst(".text");
        Element image = info.selectFirst(CssSelectors.TAG_IMG);
        if (image != null) {
            item.setCover(NetUtils.createURL(image.attr(CssSelectors.ATTR_SRC)));
        }
        Elements as = info.select(CssSelectors.TAG_A);
        List<Link> resources = new LinkedList<>();
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
     * Retrieves the page of vod indices by the given type.
     *
     * @see GrapeVodType
     */
    @Nonnull
    public AmountCountablePage<GrapeVodIndex> findAll(@Nonnull GrapeVodType type,
        GrapeOrderBy orderBy, @Nonnull PageIndex pageIndex)
        throws NotFoundException, OtherResponseException {
        String order = Optional.ofNullable(orderBy).orElse(GrapeOrderBy.TIME).getAsPath();
        int page = pageIndex.getCurrent() + 1;
        String arg = String.format("vod-type-id-%d-order-%s-p-%d", type.getId(), order, page);
        RequestBuilder builder = httpGet("/index.php").addParameter("s", arg);
        Document document = getDocument(builder);
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
            LocalDate date = LocalDate.parse(((TextNode) node).text().strip());
            GrapeVodItem vodItem = new GrapeVodItem(path, title, type, date.atStartOfDay());
            vodItem.setState(li.selectFirst(".mod_version").text());
            indices.add(vodItem);
        }
        return Page.amountCountable(indices, pageIndex, 35, total);
    }

    /**
     * Retrieves a vod item by the given index.
     *
     * @see GrapeVodIndex#getAsPath()
     */
    @Nonnull
    public GrapeVodItem findVodItem(@Nonnull String path)
        throws NotFoundException, OtherResponseException {
        Document document = getDocument(httpGet(path));

        Elements heads = document.selectFirst(".bread-crumbs").select(CssSelectors.TAG_A);
        String typeHref = heads.get(1).attr(CssSelectors.ATTR_HREF);
        String typeText = RegexUtils.matchesOrElseThrow(Lazy.TYPE_HREF_REGEX, typeHref).group("t");
        GrapeVodType type = EnumUtilExt
            .valueOfKey(GrapeVodType.class, typeText, GrapeVodType::getAsPath);
        String timeStr = document.selectFirst("#addtime").text().strip();
        LocalDateTime addTime = LocalDateTime.parse(timeStr, Constants.YYYY_MM_DD_HH_MM);
        Element div = document.selectFirst(".detail-title");
        Element h1 = div.selectFirst(CssSelectors.TAG_H);
        GrapeVodItem item = new GrapeVodItem(path, h1.text().strip(), type, addTime);

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
        List<Link> resources = new ArrayList<>();
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

    @Override
    public <T> T execute(@Nonnull HttpRequest request, ResponseHandler<? extends T> responseHandler)
        throws IOException {
        try {
            return super.execute(request, responseHandler);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
                throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, e.getReasonPhrase());
            }
            throw e;
        }
    }

    private static class Lazy {

        private static final Pattern VOD_REGEX = Pattern
            .compile("tt\\d+/?|/vod/\\d+/?|/vod/[a-z]+/[a-z]+/?|/html/\\d+\\.html");
        private static final Pattern PAGE_SUM_REGEX = Pattern.compile("共(?<t>\\d+)部 :\\d+/\\d+");
        private static final Pattern TYPE_HREF_REGEX;

        static {
            String types = Arrays.stream(GrapeVodType.values()).map(PathSupplier::getAsPath)
                .collect(Collectors.joining("|"));
            TYPE_HREF_REGEX = Pattern.compile("/vod/list/(?<t>" + types + ")/");
        }
    }
}
