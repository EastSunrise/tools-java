package wsg.tools.internet.resource.movie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.IntRangeIdentifiedRepositoryImpl;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.IntRangeIdentifiedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.Scheme;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.LinkFactory;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.impl.Thunder;

/**
 * @author Kingen
 * @see <a href="http://m.y80s.org">80s</a>
 * @since 2020/9/9
 */
@Slf4j
public final class Y80sSite extends BaseSite implements Repository<Integer, Y80sItem> {

    private static final Pattern TYPE_HREF_REGEX;
    private static final Pattern PLAY_HREF_REGEX;
    private static final Pattern MOVIE_HREF_REGEX = Pattern
        .compile("//m\\.y80s\\.com/movie/(?<id>\\d+)");
    private static final Pattern YEAR_REGEX = Pattern
        .compile("-?(?<y>\\d{4})(年|-\\d{2}-\\d{2})?|未知|\\d\\.\\d|\\d{5}|\\d{1,3}|");
    private static final Pattern DOUBAN_HREF_REGEX = Pattern
        .compile("//movie\\.douban\\.com/subject/((?<id>\\d+)( +|/|c|v|)|[^\\d].*?|)/reviews");

    static {
        String types = Arrays.stream(Y80sType.values()).map(TextSupplier::getText)
            .collect(Collectors.joining("|"));
        TYPE_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com/(?<t>" + types + ")/\\d+(-\\d){6}");
        PLAY_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com/(?<t>" + types + ")/\\d+/play-\\d+");
    }

    public Y80sSite() {
        super("80s", new BasicHttpSession(Scheme.HTTP, "y80s.org"));
    }

    /**
     * Returns the repository from 1 to {@link #max()}.
     */
    public IntRangeIdentifiedRepository<Y80sItem> getRepository() throws HttpResponseException {
        return new IntRangeIdentifiedRepositoryImpl<>(this, max());
    }

    /**
     * @see <a href="http://m.y80s.com/movie/1-0-0-0-0-0-0">Last Update Movie</a>
     */
    @Nonnull
    public Integer max() throws HttpResponseException {
        RequestBuilder builder = builder("m", "/movie/1-0-0-0-0-0-0");
        Document document = getDocument(builder, SnapshotStrategy.always());
        Elements list = document.select(".list_mov");
        int max = 1;
        for (Element div : list) {
            String href = div.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
            String id = RegexUtils.matchesOrElseThrow(MOVIE_HREF_REGEX, href).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    @Override
    public Y80sItem findById(@Nonnull Integer id) throws HttpResponseException {
        RequestBuilder builder = builder("m", "/movie/%d", id);
        Document document = getDocument(builder, SnapshotStrategy.never());
        if (document.childNodes().size() == 1) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, "Target page is empty.");
        }

        Map<String, Element> info = document.select(".movie_attr").stream()
            .collect(Collectors.toMap(Element::text, e -> e));
        Elements lis = document.selectFirst("#path").select(CssSelectors.TAG_LI);
        String typeHref = lis.get(1).selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
        Matcher typeMatcher = RegexUtils.matchesOrElseThrow(TYPE_HREF_REGEX, typeHref);
        Y80sType type = EnumUtilExt.deserializeText(typeMatcher.group("t"), Y80sType.class, false);
        String dateStr = ((TextNode) info.get("资源更新：").nextSibling()).text().strip();
        LocalDate updateDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        Y80sItem item = new Y80sItem(id, builder.toString(), updateDate, type);

        item.setTitle(lis.last().text().strip());
        Element yearEle = info.get("年代：");
        if (yearEle != null) {
            String year = RegexUtils
                .matchesOrElseThrow(YEAR_REGEX, yearEle.nextElementSibling().text()).group("y");
            if (year != null) {
                item.setYear(Integer.parseInt(year));
            }
        }
        Element dbEle = info.get("豆瓣评分：");
        if (dbEle != null) {
            String doubanHref = dbEle.nextElementSibling().nextElementSibling()
                .attr(CssSelectors.ATTR_HREF);
            String dbId = RegexUtils.matchesOrElseThrow(DOUBAN_HREF_REGEX, doubanHref).group("id");
            if (dbId != null) {
                item.setDbId(Long.parseLong(dbId));
            }
        }

        List<AbstractLink> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        Elements trs = document.select("#dl-tab-panes").select(CssSelectors.TAG_TR);
        for (Element tr : trs) {
            Element a = tr.selectFirst(CssSelectors.TAG_A);
            String href = a.attr(CssSelectors.ATTR_HREF);
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href)) {
                continue;
            }
            if (PLAY_HREF_REGEX.matcher(href).matches()) {
                href = Scheme.HTTP + Constants.URL_SCHEME_SEPARATOR + href;
            }
            String title = a.text().strip();
            try {
                resources
                    .add(LinkFactory.create(title, href, () -> LinkFactory.getPassword(title)));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setLinks(resources);
        item.setExceptions(exceptions);
        return item;
    }
}
