package wsg.tools.internet.resource.movie;

import java.net.URL;
import java.time.LocalDate;
import java.time.Year;
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
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.Repository;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.RequestBuilder;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.StringResponseHandler;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.LinkFactory;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.impl.Thunder;
import wsg.tools.internet.movie.common.VideoConstants;

/**
 * @author Kingen
 * @see <a href="https://www.993dy.com/">Movie Heaven</a>
 * @see <a href="https://www.993vod.com/">Backup</a>
 * @since 2020/10/18
 */
@Slf4j
public final class MovieHeavenSite extends BaseSite implements
    Repository<Integer, MovieHeavenItem> {

    private static final String TIP_TITLE = "系统提示";
    private static final String UNKNOWN_YEAR = "未知";
    private static final String XUNLEI = "xunlei";
    private static final String URL_SEPARATOR = "#";
    private static final String EXTRA_COVER_HEAD = "https://img22.qayqa.com:6868http";

    public MovieHeavenSite() {
        super("Movie Heaven", new BasicHttpSession("993vod.com"), new MovieHeaverResponseHandler());
    }

    /**
     * Returns the repository of all items from 1 to {@link #latest()}. Only several items are not
     * found.
     */
    public ListRepository<Integer, MovieHeavenItem> getRepository() throws OtherResponseException {
        Stream<Integer> stream = IntStream.rangeClosed(1, latest()).boxed();
        return Repositories.list(this, stream.collect(Collectors.toList()));
    }

    /**
     * @see <a href="https://www.993vod.com/">Home</a>
     */
    public int latest() throws OtherResponseException {
        Document document = findDocument(builder0(""), SnapshotStrategy.always());
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
    public MovieHeavenItem findById(Integer id) throws NotFoundException, OtherResponseException {
        Objects.requireNonNull(id);
        RequestBuilder builder = builder0("/vod-detail-id-%d.html", id);
        Document doc = getDocument(builder, SnapshotStrategy.never());
        if (TIP_TITLE.equals(doc.title())) {
            String message = doc.selectFirst("h4.infotitle1").text();
            throw new NotFoundException(message);
        }
        Map<String, Element> info = doc.selectFirst("div.info").select(CssSelectors.TAG_SPAN)
            .stream().collect(Collectors.toMap(Element::text, e -> e));

        Elements children = doc.selectFirst(".location").children();
        String typeHref = children.get(1).attr(CssSelectors.ATTR_HREF);
        Matcher typeMatcher = RegexUtils.matchesOrElseThrow(Lazy.TYPE_HREF_REGEX, typeHref);
        int typeId = Integer.parseInt(typeMatcher.group("id"));
        MovieHeavenType type = EnumUtilExt.valueOfCode(typeId, MovieHeavenType.class);
        String timeText = ((TextNode) info.get("上架时间：").nextSibling()).text();
        LocalDate addDate = LocalDate.parse(timeText, DateTimeFormatter.ISO_LOCAL_DATE);
        Element image = doc.selectFirst(".pic").selectFirst(CssSelectors.TAG_IMG);
        String src = image.attr(CssSelectors.ATTR_SRC);
        if (src.startsWith(EXTRA_COVER_HEAD)) {
            src = src.substring(EXTRA_COVER_HEAD.length() - 4);
        }
        URL cover = NetUtils.createURL(src);
        MovieHeavenItem item = new MovieHeavenItem(id, builder.toString(), type, addDate, cover);

        item.setTitle(children.last().text());
        String text = ((TextNode) info.get("上映年代：").nextSibling()).text();
        if (StringUtils.isNotBlank(text) && !UNKNOWN_YEAR.equals(text)) {
            int year = Integer.parseInt(text);
            if (year >= VideoConstants.MOVIE_START_YEAR && year <= Year.now().getValue()) {
                item.setYear(year);
            }
        }
        Node node = info.get("状态：").nextSibling();
        if (node != null) {
            item.setState(((TextNode) node).text().strip());
        }

        List<AbstractLink> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        final String downUl = "ul.downurl";
        for (Element ul : doc.select(downUl)) {
            Element script = ul.selectFirst(CssSelectors.TAG_SCRIPT);
            String varUrls = script.html().strip().split("\n")[0].strip();
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.VAR_URL_REGEX, varUrls);
            String entries = StringEscapeUtils.unescapeHtml4(matcher.group("entries"));
            for (String entry : entries.split(URL_SEPARATOR)) {
                Matcher rMatcher = RegexUtils.matchesOrElseThrow(Lazy.RESOURCE_REGEX, entry);
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

    private static class Lazy {

        private static final Pattern ITEM_HREF_REGEX = Pattern
            .compile("/vod-detail-id-(?<id>\\d+)\\.html");
        private static final Pattern TYPE_HREF_REGEX;
        private static final Pattern RESOURCE_REGEX = Pattern
            .compile("(?<t>(第\\d+集\\$)?[^$]+)\\$(?<u>[^$]*)");
        private static final Pattern VAR_URL_REGEX = Pattern
            .compile("var downurls=\"(?<entries>.*)#\";");

        static {
            String types = Arrays.stream(MovieHeavenType.values()).map(IntCodeSupplier::getCode)
                .map(String::valueOf).collect(Collectors.joining("|"));
            TYPE_HREF_REGEX = Pattern.compile("/vod-type-id-(?<id>" + types + ")-pg-1\\.html");
        }
    }

    private static class MovieHeaverResponseHandler extends StringResponseHandler {

        private static final String ILLEGAL_ARGUMENT = "您的提交带有不合法参数,谢谢合作!";

        @Override
        protected String handleContent(String content) throws HttpResponseException {
            if (StringUtils.contains(content, ILLEGAL_ARGUMENT)) {
                throw new HttpResponseException(HttpStatus.SC_FORBIDDEN, ILLEGAL_ARGUMENT);
            }
            return content;
        }
    }
}
