package wsg.tools.internet.resource.movie;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.RequestBuilder;
import wsg.tools.internet.base.support.SnapshotStrategies;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
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
@ConcreteSite
public final class Y80sSite extends AbstractListResourceSite<Y80sItem> {

    private static final int MIN_ID = 32;
    private static final Range<Integer> EXCEPTS = Range.between(1501, 3008);

    public Y80sSite() {
        super("80s", new BasicHttpSession(Scheme.HTTP, "y80s.org"));
    }

    /**
     * Returns the repository of all items from 1 to {@link #latest()} <strong>except those in
     * {@link #EXCEPTS}</strong>.
     */
    @Override
    @Nonnull
    public ListRepository<Integer, Y80sItem> getRepository() throws OtherResponseException {
        IntStream stream = IntStream.rangeClosed(MIN_ID, latest())
            .filter(i -> !EXCEPTS.contains(i));
        return Repositories.list(this, stream.boxed().collect(Collectors.toList()));
    }

    /**
     * @see <a href="http://m.y80s.com/movie/1-0-0-0-0-0-0">Last Update Movie</a>
     */
    public int latest() throws OtherResponseException {
        RequestBuilder builder = builder("m", "/movie/1-0-0-0-0-0-0");
        Document document = findDocument(builder, SnapshotStrategies.always());
        Elements list = document.select(".list_mov");
        int max = 1;
        for (Element div : list) {
            String href = div.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
            String id = RegexUtils.matchesOrElseThrow(Lazy.MOVIE_HREF_REGEX, href).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    @Nonnull
    @Override
    public Y80sItem findById(Integer id) throws NotFoundException, OtherResponseException {
        Objects.requireNonNull(id);
        RequestBuilder builder = builder("m", "/movie/%d", id);
        Document document = getDocument(builder, SnapshotStrategies.never());
        if (document.childNodes().size() == 1) {
            throw new NotFoundException("Target page is empty.");
        }

        Map<String, Element> info = document.select(".movie_attr").stream()
            .collect(Collectors.toMap(Element::text, e -> e));
        Elements lis = document.selectFirst("#path").select(CssSelectors.TAG_LI);
        String typeHref = lis.get(1).selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
        String typeStr = RegexUtils.matchesOrElseThrow(Lazy.TYPE_HREF_REGEX, typeHref).group("t");
        Y80sType realType = EnumUtilExt.valueOfText(Y80sType.class, typeStr, false);
        String dateStr = ((TextNode) info.get("资源更新：").nextSibling()).text().strip();
        LocalDate updateDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        Y80sItem item = new Y80sItem(id, builder.toString(), realType, updateDate);

        item.setTitle(lis.last().text().strip());
        String src = document.selectFirst(".img-responsive").attr(CssSelectors.ATTR_SRC);
        item.setCover(NetUtils.createURL(Constants.HTTP_SCHEME + src));
        Node first = document.selectFirst(".movie-h1").selectFirst("small").childNode(0);
        Matcher matcher = Lazy.YEAR_REGEX.matcher(((TextNode) first).text());
        if (matcher.find()) {
            item.setYear(Integer.parseInt(matcher.group()));
        }
        Element dbEle = info.get("豆瓣评分：");
        if (dbEle != null) {
            dbEle = dbEle.nextElementSibling().nextElementSibling();
            Matcher dbMatcher = Lazy.DOUBAN_HREF_REGEX.matcher(dbEle.attr(CssSelectors.ATTR_HREF));
            if (dbMatcher.find()) {
                item.setDbId(Long.parseLong(dbMatcher.group("id")));
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
            if (Lazy.PLAY_HREF_REGEX.matcher(href).matches()) {
                href = Constants.HTTP_SCHEME + href;
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

    private static class Lazy {

        private static final Pattern MOVIE_HREF_REGEX = Pattern
            .compile("//m\\.y80s\\.com/movie/(?<id>\\d+)");
        private static final Pattern TYPE_HREF_REGEX;
        private static final Pattern PLAY_HREF_REGEX;
        private static final Pattern YEAR_REGEX = Pattern.compile("(?<y>\\d{4,5})");
        private static final Pattern DOUBAN_HREF_REGEX = Pattern.compile("/subject/(?<id>\\d+)");

        static {
            String types = Arrays.stream(Y80sType.values()).map(TextSupplier::getText)
                .collect(Collectors.joining("|"));
            TYPE_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com/(?<t>" + types + ")/\\d+(-\\d){6}");
            PLAY_HREF_REGEX = Pattern
                .compile("//m\\.y80s\\.com/(?<t>" + types + ")/\\d+/play-\\d+");
        }
    }
}
