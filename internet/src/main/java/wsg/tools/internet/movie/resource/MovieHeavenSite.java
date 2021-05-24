package wsg.tools.internet.movie.resource;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.RequestBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.net.NetUtils;
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
 * @see <a href="https://www.993dy.com/">Movie Heaven</a>
 * @see <a href="https://www.993vod.com/">Backup</a>
 * @since 2020/10/18
 */
@ConcreteSite
@Slf4j
public final class MovieHeavenSite extends AbstractListResourceSite<MovieHeavenItem> {

    private static final String TIP_TITLE = "系统提示";
    private static final String UNKNOWN_YEAR = "未知";
    private static final String XUNLEI = "xunlei";
    private static final String URL_SEPARATOR = "#";
    private static final String EXTRA_COVER_HEAD = "https://img22.qayqa.com:6868http";
    private static final String ILLEGAL_ARGUMENT = "您的提交带有不合法参数,谢谢合作!";

    public MovieHeavenSite() {
        super("Movie Heaven", httpsHost("www.993dy.com"));
    }

    /**
     * Returns the repository of all items from 1 to {@link #latest()}. Only several items are not
     * found.
     */
    @Override
    @Nonnull
    public ListRepository<Integer, MovieHeavenItem> getRepository() throws OtherResponseException {
        Stream<Integer> stream = IntStream.rangeClosed(1, latest()).boxed();
        return Repositories.list(this, stream.collect(Collectors.toList()));
    }

    /**
     * @see <a href="https://www.993vod.com/">Home</a>
     */
    public int latest() throws OtherResponseException {
        Document document = findDocument(httpGet(""));
        Elements lis = document.selectFirst("div.newbox").select(CssSelectors.TAG_LI);
        int max = 1;
        for (Element li : lis) {
            String href = li.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
            String id = RegexUtils.matchesOrElseThrow(Lazy.ITEM_HREF_REGEX, href).group("id");
            max = Math.max(max, Integer.parseInt(id));
        }
        return max;
    }

    @Nonnull
    @Override
    public MovieHeavenItem findById(@Nonnull Integer id)
        throws NotFoundException, OtherResponseException {
        Document document = getDocument(httpGet("/vod-detail-id-%d.html", id));
        if (TIP_TITLE.equals(document.title())) {
            String message = document.selectFirst("h4.infotitle1").text();
            throw new NotFoundException(message);
        }
        Map<String, Element> info = document.selectFirst("div.info").select(CssSelectors.TAG_SPAN)
            .stream().collect(Collectors.toMap(Element::text, e -> e));

        String timeText = ((TextNode) info.get("更新日期：").nextSibling()).text();
        LocalDate date = LocalDate.parse(timeText, DateTimeFormatter.ISO_LOCAL_DATE);
        Element image = document.selectFirst(".pic").selectFirst(CssSelectors.TAG_IMG);
        String src = image.attr(CssSelectors.ATTR_SRC);
        if (src.startsWith(EXTRA_COVER_HEAD)) {
            src = src.substring(EXTRA_COVER_HEAD.length() - 4);
        }
        URL cover = NetUtils.createURL(src);
        Pair<MovieHeavenType, ResourceState> pair = getTypeAndState(document);
        String title = document.selectFirst(".location").children().last().text();
        MovieHeavenItem item = new MovieHeavenItem(pair.getLeft(), id, title, date, cover);

        item.setState(pair.getRight());
        String text = ((TextNode) info.get("上映年代：").nextSibling()).text().strip();
        if (StringUtils.isNotBlank(text) && !UNKNOWN_YEAR.equals(text)) {
            int year = Integer.parseInt(text);
            if (year >= VideoConstants.MOVIE_START_YEAR && year <= Year.now().getValue()) {
                item.setYear(year);
            }
        }

        List<Link> resources = new ArrayList<>();
        List<InvalidResourceException> exceptions = new ArrayList<>();
        final String downUl = "ul.downurl";
        for (Element ul : document.select(downUl)) {
            Element script = ul.selectFirst(CssSelectors.TAG_SCRIPT);
            String varUrls = script.html().strip().split("\n")[0].strip();
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.VAR_URL_REGEX, varUrls);
            String entries = StringEscapeUtils.unescapeHtml4(matcher.group("entries"));
            for (String entry : entries.split(URL_SEPARATOR)) {
                Matcher rMatcher = Lazy.RESOURCE_REGEX.matcher(entry);
                if (!rMatcher.matches()) {
                    continue;
                }
                String url = rMatcher.group("u");
                if (StringUtils.isBlank(url) || Thunder.EMPTY_LINK.equals(url)) {
                    continue;
                }
                String t = rMatcher.group("t");
                if (XUNLEI.equals(url)) {
                    url = t;
                    t = XUNLEI;
                }
                try {
                    String ft = t;
                    resources.add(LinkFactory.create(t, url, () -> LinkFactory.getPassword(ft)));
                } catch (InvalidResourceException e) {
                    exceptions.add(e);
                }
            }
        }
        item.setLinks(resources);
        item.setExceptions(exceptions);
        return item;
    }

    private Pair<MovieHeavenType, ResourceState> getTypeAndState(Document document) {
        Elements children = document.selectFirst(".location").children();
        String href = children.get(1).attr(CssSelectors.ATTR_HREF);
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.TYPE_HREF_REGEX, href);
        int typeId = Integer.parseInt(matcher.group("id"));
        MovieHeavenType type = EnumUtilExt.valueOfIntCode(MovieHeavenType.class, typeId);
        if (type.isMovie()) {
            return Pair.of(type, ResourceState.FINISHED);
        }
        Elements spans = document.selectFirst("div.info").select(CssSelectors.TAG_SPAN);
        for (Element span : spans) {
            if ("状态：".equals(span.text())) {
                Node node = span.nextSibling();
                if (node == null) {
                    return Pair.of(type, null);
                }
                String text = ((TextNode) node).text().strip();
                if (text.contains("全") || text.contains("完结") ||
                    text.contains("HD") || text.contains("BD") || text.contains("DVD")) {
                    return Pair.of(type, ResourceState.FINISHED);
                }
                if (text.contains("至") || text.contains("第") || text.contains("更新")) {
                    return Pair.of(type, ResourceState.UPDATING);
                }
                return Pair.of(type, ResourceState.UNKNOWN);
            }
        }
        throw new UnexpectedContentException("Can't find state");
    }

    @Override
    public String getContent(@Nonnull RequestBuilder builder) throws IOException {
        String content = super.getContent(builder);
        if (StringUtils.contains(content, ILLEGAL_ARGUMENT)) {
            throw new HttpResponseException(HttpStatus.SC_FORBIDDEN, ILLEGAL_ARGUMENT);
        }
        return content;
    }

    private static class Lazy {

        private static final Pattern ITEM_HREF_REGEX = Pattern
            .compile("/vod-detail-id-(?<id>\\d+)\\.html");
        private static final Pattern TYPE_HREF_REGEX;
        private static final Pattern RESOURCE_REGEX = Pattern
            .compile("(?<t>(第\\d+集\\$)?[^$]+)\\$(?<u>[^$]*)");
        private static final Pattern VAR_URL_REGEX = Pattern
            .compile("var downurls=\"(?<entries>.*)#?\";");

        static {
            String types = Arrays.stream(MovieHeavenType.values()).map(IntCodeSupplier::getCode)
                .map(String::valueOf).collect(Collectors.joining("|"));
            TYPE_HREF_REGEX = Pattern.compile("/vod-type-id-(?<id>" + types + ")-pg-1\\.html");
        }
    }
}
