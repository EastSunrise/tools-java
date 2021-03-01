package wsg.tools.internet.resource.site;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseRepository;
import wsg.tools.internet.base.IntRangeRepositoryImpl;
import wsg.tools.internet.base.RequestBuilder;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.impl.Ed2kResource;
import wsg.tools.internet.resource.impl.ResourceFactory;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Access to items of Grape Video, which include downloadable resources.
 *
 * @author Kingen
 * @since 2021/2/4
 */
public final class GrapeNewsSite extends IntRangeRepositoryImpl<GrapeNewsItem> implements BaseRepository<Integer, GrapeNewsItem> {

    private static final Pattern TIME_REGEX = Pattern.compile("发布时间：(?<s>\\d{4}-\\d{2}-\\d{2})");
    private static final Pattern YEAR_REGEX = Pattern.compile("◎年\\s*代\\s+(?<y>\\d{4})\\s*◎");
    private static final Pattern VOD_REGEX = Pattern.compile("tt\\d+/?|/vod/\\d+/?|/vod/[a-z]+/[a-z]+/?|/html/\\d+\\.html");
    private static final String BT_ATTACH = "url_btbtt";
    private static GrapeNewsSite instance;

    private GrapeNewsSite() {
        super("Grape News", "putaoys.com");
    }

    public static GrapeNewsSite getInstance() {
        if (instance == null) {
            instance = new GrapeNewsSite();
        }
        return instance;
    }

    @Override
    protected int max() {
        return 16132;
    }

    @Override
    protected GrapeNewsItem getItem(int id) throws HttpResponseException {
        RequestBuilder builder = builder0("/movie/%d.html", id);
        Document document = getDocument(builder, SnapshotStrategy.NEVER_UPDATE);

        LocalDate releaseDate = LocalDate.parse(RegexUtils.matchesOrElseThrow(TIME_REGEX, document.selectFirst(".updatetime").text()).group("s"));
        GrapeNewsItem item = new GrapeNewsItem(id, builder.toString(), releaseDate);

        item.setTitle(document.selectFirst("div.news_title_all").selectFirst(CssSelector.TAG_H1).text().strip());
        Element info = document.selectFirst(".text");
        Matcher matcher = YEAR_REGEX.matcher(info.text());
        if (matcher.find()) {
            item.setYear(Integer.parseInt(matcher.group("y")));
        }

        Elements as = info.select(CssSelector.TAG_A);
        List<AbstractResource> resources = new LinkedList<>();
        List<InvalidResourceException> exceptions = new LinkedList<>();
        for (Element a : as) {
            try {
                String href = a.attr(CssSelector.ATTR_HREF).strip();
                if (StringUtils.isBlank(href) || VOD_REGEX.matcher(href).matches()) {
                    continue;
                }
                if (href.contains(BT_ATTACH)) {
                    href = href.replace("thunder://url_btbtt", "http://51btbtt.com/attach-download");
                }
                int index = StringUtils.indexOf(href, Ed2kResource.ED2K_PREFIX);
                if (StringUtils.INDEX_NOT_FOUND != index) {
                    href = href.substring(index);
                }
                resources.add(ResourceFactory.create(a.text().strip(), href, () -> {
                    Node node = a.nextSibling();
                    if (node instanceof TextNode) {
                        return ResourceFactory.getPassword(((TextNode) node).text());
                    }
                    return null;
                }));
            } catch (InvalidResourceException e) {
                exceptions.add(e);
            }
        }
        item.setResources(resources);
        item.setExceptions(exceptions);
        return item;
    }
}
