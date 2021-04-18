package wsg.tools.internet.info.adult.ggg;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.repository.RepoPageable;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.RequestWrapper;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * @author Kingen
 * @see <a href="http://buzz.ggg-av.com/">GGG-AV</a>
 * @since 2021/4/13
 */
public class GggSite extends BaseSite implements RepoPageable<GggPageReq, GggPageResult>,
    RepoRetrievable<Integer, GggGoodView> {

    private static final String HOME = "http://buzz.ggg-av.com";
    private static final String BLANK_IMAGE = "images/goods_blank.jpg";

    public GggSite() {
        super("GGG AV", httpHost("buzz.ggg-av.com"));
    }

    /**
     * Retrieves all categories.
     */
    public List<GggCategory> findAllCategories() throws OtherResponseException {
        Document document = findDocument(httpGet("/home/index.php"), t -> true);
        List<GggCategory> categories = new ArrayList<>();
        Element div = document.selectFirst("#CategoryDiv");
        categories.addAll(getCategories(div.selectFirst("#category0"), false));
        categories.addAll(getCategories(div.selectFirst("#category1"), true));
        categories.addAll(getCategories(div.selectFirst("#category2"), true));
        return categories;
    }

    private List<GggCategory> getCategories(Element tr, boolean mosaic) {
        Elements list = tr.select(".CategoryList");
        List<GggCategory> categories = new ArrayList<>(list.size());
        for (Element a : list) {
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.CATEGORY_HREF_REGEX, href);
            int code = Integer.parseInt(matcher.group("c"));
            categories.add(new GggCategory(code, mosaic, a.text()));
        }
        return categories;
    }

    @Nonnull
    @Override
    public GggPageResult findPage(@Nonnull GggPageReq req)
        throws NotFoundException, OtherResponseException {
        RequestWrapper wrapper = httpGet("/home/index.php")
            .addParameter("c", "CategoryAction")
            .addParameter("m", "listDetail")
            .addParameter("categoryNo", req.getCategory().getCode())
            .addParameter("orderField", req.getOrder())
            .addParameter("page", req.getCurrent() + 1);
        Document document = getDocument(wrapper, t -> true);
        Elements tables = document.selectFirst("#GoodsCarForm").select(".TableStyle");
        List<GggGood> goods = new ArrayList<>(tables.size());
        for (Element table : tables) {
            Element p = table.selectFirst(CssSelectors.TAG_P);
            Element a = p.selectFirst(CssSelectors.TAG_A);
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.GOOD_HREF_REGEX, href);
            int id = Integer.parseInt(matcher.group("id"));
            Element span = a.selectFirst(CssSelectors.TAG_SPAN);
            String title = span == null ? a.text().strip() : span.attr(CssSelectors.ATTR_TITLE);
            Elements info = p.nextElementSibling().child(0).children();
            Iterator<Element> iterator = info.iterator();
            iterator.next();
            String code = ((TextNode) iterator.next().nextSibling()).text().strip();
            if (code.isBlank()) {
                continue;
            }
            iterator.next();
            String date = ((TextNode) iterator.next().nextSibling()).text().strip();
            LocalDate publish = LocalDate.parse(date, Lazy.FORMATTER);
            boolean mosaic = req.getCategory().isMosaic();
            GggGood good = new GggGood(id, code, title, mosaic, publish);
            String src = table.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);
            if (!BLANK_IMAGE.equals(src)) {
                good.setImage(NetUtils.createURL(HOME + src.substring(2)));
            }
            good.setTags(collect(iterator).toArray(new String[0]));
            good.setDistributor(getText(iterator.next()));
            good.setActresses(collect(iterator));
            iterator.next();
            Element font = iterator.next();
            String description = font.text();
            if (description.length() > 5) {
                good.setDescription(description.substring(5).strip());
            }
            goods.add(good);
        }
        return new GggPageResult(goods, req);
    }

    /**
     * Retrieves the view of the good of the specified id.
     *
     * @see GggGood#getId()
     */
    @Nonnull
    @Override
    public GggGoodView findById(@Nonnull Integer id)
        throws NotFoundException, OtherResponseException {
        Document document = getDocument(httpGet("/home/goods-view-%d", id), t -> false);
        Element table1 = document.selectFirst("#table1");
        Elements tds = table1.selectFirst("#table7").select(CssSelectors.TAG_TD);
        String title = tds.first().text().strip();
        String src = tds.get(1).selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);
        URL cover = NetUtils.createURL(HOME + src.substring(2));
        String description = tds.last().text();
        GggGoodView view = new GggGoodView(id, title, cover, description);
        Element p = table1.select(CssSelectors.TAG_TR).last().selectFirst(CssSelectors.TAG_P);
        Element img = p.selectFirst(CssSelectors.TAG_IMG);
        if (img != null) {
            view.setImage(NetUtils.createURL(HOME + img.attr(CssSelectors.ATTR_SRC).substring(2)));
        }
        return view;
    }

    private String getText(Element span) {
        Node node = span.nextSibling();
        if (node instanceof TextNode) {
            String text = ((TextNode) node).text();
            return text.isBlank() ? null : text;
        }
        return null;
    }

    private List<String> collect(Iterator<Element> iterator) {
        iterator.next();
        iterator.next();
        List<String> values = new ArrayList<>();
        while (true) {
            Element current = iterator.next();
            if (current.is(CssSelectors.TAG_BR)) {
                break;
            }
            values.add(current.text().strip());
        }
        return values;
    }

    private static class Lazy {

        private static final Pattern CATEGORY_HREF_REGEX = Pattern
            .compile("\\./index\\.php\\?c=CategoryAction&m=listDetail&categoryNo=(?<c>\\d+)");
        private static final Pattern GOOD_HREF_REGEX = Pattern.compile("goods-view-(?<id>\\d+)");
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("yyyy / MM / dd");
    }
}
