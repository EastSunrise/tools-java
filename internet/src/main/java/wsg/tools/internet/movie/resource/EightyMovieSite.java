package wsg.tools.internet.movie.resource;

import java.io.IOException;
import java.time.LocalDate;
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
import javax.annotation.Nonnull;
import org.apache.http.client.methods.RequestBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.Constants;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.WrappedStringResponseHandler;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.support.InvalidResourceException;
import wsg.tools.internet.download.support.LinkFactory;

/**
 * @author Kingen
 * @see <a href="https://www.80sdy1.com/">80s Movies</a>
 * @since 2021/6/15
 */
public class EightyMovieSite extends BaseSite implements RepoRetrievable<EightyIndex, EightyItem> {

    private static final String NO_POSTER = "/images/load.png";

    public EightyMovieSite() {
        super("80s Movie", httpsHost("www.80sdy1.com"));
    }

    /**
     * Returns the repository that contains all items of current site.
     *
     * @return the list repository
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    public ListRepository<Integer, EightyItem> getRepository(@Nonnull EightyType type)
        throws OtherResponseException {
        List<Integer> ids = IntStream.rangeClosed(1, latest(type)).boxed()
            .collect(Collectors.toList());
        return Repositories.list(id -> this.findById(new EightyIndex(type, id)), ids);
    }

    private int latest(EightyType type) throws OtherResponseException {
        Document document = findDocument(httpGet("/%s/index.html", type.getAsPath()));
        Elements list = document.select(".list_mov");
        int max = 1;
        for (Element div : list) {
            String href = div.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.ITEM_HREF_REGEX, href);
            max = Math.max(max, Integer.parseInt(matcher.group("id")));
        }
        return max;
    }

    @Nonnull
    @Override
    public EightyItem findById(@Nonnull EightyIndex key)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = httpGet("/%s/%d.html", key.getType().getAsPath(), key.getId());
        Document document = getDocument(builder);
        Element h1 = document.selectFirst("#movie_name");
        String title = ((TextNode) h1.childNode(0)).text();
        int year = Integer.parseInt(((TextNode) h1.child(0).childNode(0)).text().substring(1, 5));
        Elements list = document.select(".DownList");
        List<Link> links = new ArrayList<>(list.size());
        List<InvalidResourceException> exs = new ArrayList<>(0);
        for (Element div : list) {
            Element a = div.child(0);
            try {
                links.add(LinkFactory.create(a.text(), a.attr(CssSelectors.ATTR_HREF)));
            } catch (InvalidResourceException e) {
                exs.add(e);
            }
        }
        Elements lis = document.select(".movie-info-li");
        Map<String, Element> info = new HashMap<>(lis.size());
        for (Element li : lis) {
            Element span = li.child(0);
            info.put(span.text(), span);
        }
        Element span = info.get("资源更新：");
        LocalDate date = LocalDate.parse(((TextNode) span.nextSibling()).text(), Lazy.FORMATTER);
        EightyItem item = new EightyItem(key.getId(), key.getType(), title, links, exs, year, date);
        String[] parts = document.selectFirst("#movie_aka").text().substring(3).split("/");
        item.setAka(Arrays.stream(parts).map(String::strip).collect(Collectors.toList()));
        String src = document.selectFirst("#poster").child(0).attr(CssSelectors.ATTR_SRC);
        if (!NO_POSTER.equals(src)) {
            item.setCover(NetUtils.createURL(this.getHost().toURI() + src));
        }
        return item;
    }

    @Override
    public String getContent(@Nonnull RequestBuilder builder) throws IOException {
        WrappedStringResponseHandler handler = new WrappedStringResponseHandler(Constants.GBK);
        return getResponseWrapper(builder, handler).getContent();
    }

    private static class Lazy {

        private static final Pattern ITEM_HREF_REGEX;
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/M/d");

        static {
            String types = Arrays.stream(EightyType.values()).map(PathSupplier::getAsPath)
                .collect(Collectors.joining("|"));
            ITEM_HREF_REGEX = Pattern.compile("/(" + types + ")/(?<id>\\d+)\\.html");
        }
    }
}
