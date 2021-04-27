package wsg.tools.internet.movie.resource;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UnexpectedContentException;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.Thunder;
import wsg.tools.internet.download.support.InvalidResourceException;
import wsg.tools.internet.download.support.LinkFactory;
import wsg.tools.internet.movie.common.ResourceState;
import wsg.tools.internet.movie.common.VideoConstants;

/**
 * @author Kingen
 * @see <a href="https://www.xunleicang.in/">XunLeiCang</a>
 * @since 2020/9/9
 */
@Slf4j
@ConcreteSite
public final class XlcSite extends AbstractListResourceSite<XlcItem> {

    private static final int TITLE_SUFFIX_LENGTH = 14;
    private static final String NO_PIC = "nopic.jpg";
    private static final String NO_PHOTO = "nophoto_xunleicang.in.png";
    private static final String SYSTEM_TIP = "迅雷仓-系统提示";

    public XlcSite() {
        super("XLC", httpsHost("xunleicang.in"));
    }

    /**
     * Returns the repository of all items from 1 to {@link #latest()}. <strong>About 8% of the
     * items are not found.</strong>
     */
    @Override
    @Nonnull
    public ListRepository<Integer, XlcItem> getRepository() throws OtherResponseException {
        Stream<Integer> stream = IntStream.rangeClosed(1, latest()).boxed();
        return Repositories.list(this, stream.collect(Collectors.toList()));
    }

    /**
     * @see <a href="https://www.xunleicang.in/ajax-show-id-new.html">Last Update</a>
     */
    public int latest() throws OtherResponseException {
        Document document = findDocument(httpGet("/ajax-show-id-new.html"));
        Elements as = document.selectFirst("ul.f6").select(CssSelectors.TAG_A);
        int max = 1;
        for (Element a : as) {
            String href = a.attr(CssSelectors.ATTR_HREF);
            String id = RegexUtils.findOrElseThrow(Lazy.ITEM_HREF_REGEX, href).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    @Nonnull
    @Override
    public XlcItem findById(@Nonnull Integer id) throws NotFoundException, OtherResponseException {
        Document document = getDocument(httpGet("/vod-read-id-%d.html", id));
        String title = document.title();
        if (SYSTEM_TIP.equals(title)) {
            throw new NotFoundException(document.body().text().strip());
        }

        Element pLeft = document.selectFirst(".pleft");
        Map<String, Node> info = new HashMap<>(8);
        Elements elements = pLeft.selectFirst(".moviecont").select(CssSelectors.TAG_STRONG);
        for (Element strong : elements) {
            MapUtilsExt.putIfAbsent(info, strong.text(), strong.nextSibling());
        }
        String dateStr = ((TextNode) info.get("更新时间：")).text();
        LocalDate updateDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        Pair<XlcType, ResourceState> pair = getTypeAndState(document);
        XlcType type = pair.getLeft();
        ResourceState state = pair.getRight();
        title = title.substring(0, title.length() - TITLE_SUFFIX_LENGTH);
        XlcItem item = new XlcItem(type, id, title, updateDate, state);

        String cover = document.selectFirst(".pics3").attr(CssSelectors.ATTR_SRC);
        if (!cover.isBlank() && !cover.endsWith(NO_PIC) && !cover.endsWith(NO_PHOTO)) {
            item.setCover(NetUtils.createURL(cover));
        }
        Element header = pLeft.selectFirst(CssSelectors.TAG_H3).select(CssSelectors.TAG_A).last();
        Element font = header.selectFirst(CssSelectors.TAG_FONT);
        if (font != null) {
            int year = Integer.parseInt(StringUtils.strip(font.text(), "()"));
            if (year >= VideoConstants.MOVIE_START_YEAR && year <= Year.now().getValue()) {
                item.setYear(year);
            }
        }

        List<Link> resources = new ArrayList<>();
        List<InvalidResourceException> exceptions = new ArrayList<>();
        Elements lis = document.select("ul.down-list").select("li.item");
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_A);
            String href = a.attr(CssSelectors.ATTR_HREF);
            if (StringUtils.isBlank(href) || Thunder.EMPTY_LINK.equals(href)) {
                continue;
            }
            String linkTitle = a.text().strip();
            try {
                resources.add(LinkFactory
                    .create(linkTitle, href, () -> LinkFactory.getPassword(linkTitle, href)));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setLinks(resources);
        item.setExceptions(exceptions);
        return item;
    }

    private Pair<XlcType, ResourceState> getTypeAndState(Document document) {
        Element pLeft = document.selectFirst(".pleft");
        Element header = pLeft.selectFirst(CssSelectors.TAG_H3).select(CssSelectors.TAG_A).last();
        String href = header.previousElementSibling().attr(CssSelectors.ATTR_HREF);
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.TYPE_PATH_REGEX, href);
        int typeId = Integer.parseInt(matcher.group("id"));
        XlcType type = EnumUtilExt.valueOfCode(XlcType.class, typeId);
        if (type.isMovie()) {
            return Pair.of(type, ResourceState.FINISHED);
        }
        Element cont = pLeft.selectFirst(".moviecont");
        for (Element strong : cont.select(CssSelectors.TAG_STRONG)) {
            if ("状态：".equals(strong.text())) {
                String text = ((TextNode) strong.nextSibling()).text();
                ResourceState state;
                if (text.contains("全")) {
                    state = ResourceState.FINISHED;
                } else if (text.contains("至")) {
                    state = ResourceState.UPDATING;
                } else {
                    state = ResourceState.UNKNOWN;
                }
                return Pair.of(type, state);
            }
        }
        throw new UnexpectedContentException("Can't find state");
    }

    private static class Lazy {

        private static final Pattern ITEM_HREF_REGEX = Pattern
            .compile("/vod-read-id-(?<id>\\d+)\\.html");
        private static final Pattern TYPE_PATH_REGEX;

        static {
            String types = Arrays.stream(XlcType.values()).map(IntCodeSupplier::getCode)
                .map(String::valueOf).collect(Collectors.joining("|"));
            TYPE_PATH_REGEX = Pattern.compile("/vod-show-id-(?<id>" + types + ")\\.html");
        }
    }
}
