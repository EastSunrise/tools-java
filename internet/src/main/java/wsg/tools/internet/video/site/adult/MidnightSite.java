package wsg.tools.internet.video.site.adult;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.CssSelector;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.resource.site.BaseRepository;
import wsg.tools.internet.resource.site.RangeRepository;

import javax.annotation.Nullable;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://www.shenyequ.com/">Midnight Zone</a>
 * @since 2021/2/22
 */
public class MidnightSite extends BaseSite implements BaseRepository<Integer, MidnightItem>, RangeRepository<MidnightItem, LocalDateTime> {

    private static final LocalDateTime START_TIME = LocalDateTime.of(2019, 1, 1, 0, 0);
    private static final Pattern ACTRESS_ITEM_URL_REGEX = Pattern.compile("https://www\\.shenyequ\\.com/youyou/(?<id>\\d+).html");
    private static final Pattern IMG_SRC_REGEX = Pattern.compile("https://syqpic\\.hantangrx\\.com/d/file/\\d{4}-\\d{2}-\\d{2}/[a-z0-9]{32}\\.jpg");
    private static MidnightSite instance;

    private MidnightSite() {
        super("Midnight", "shenyequ.com");
    }

    public static MidnightSite getInstance() {
        if (instance == null) {
            instance = new MidnightSite();
        }
        return instance;
    }

    @Override
    public List<MidnightItem> findAll() throws HttpResponseException {
        List<MidnightItem> items = new ArrayList<>();
        for (Integer id : getAllItems().keySet()) {
            items.add(findById(id));
        }
        return items;
    }

    @Override
    public List<MidnightItem> findAllByRangeClosed(@Nullable LocalDateTime startInclusive, @Nullable LocalDateTime endInclusive) throws HttpResponseException {
        if (startInclusive == null || startInclusive.isBefore(START_TIME)) {
            startInclusive = START_TIME;
        }
        if (endInclusive == null) {
            endInclusive = LocalDateTime.now();
        }
        List<MidnightItem> items = new ArrayList<>();
        for (Integer id : getAllItems().keySet()) {
            MidnightItem item = findById(id);
            if (item.getRelease().isAfter(endInclusive)) {
                continue;
            }
            if (item.getRelease().isBefore(startInclusive)) {
                return items;
            }
            items.add(item);
        }
        return items;
    }

    @Override
    public MidnightItem findById(Integer id) throws HttpResponseException {
        Document document = getDocument(builder0("/youyou/%d.html", id), SnapshotStrategy.NEVER_UPDATE);
        String title = document.selectFirst("h1.title").text();
        List<AdultVideo> works = new ArrayList<>();
        while (true) {
            Elements ps = document.selectFirst("div.single-content").select("p");
            for (Element p : ps) {
                Element img = p.selectFirst("img");
                if (img != null) {
                    String alt = img.attr("alt").strip();
                    if (alt.endsWith(".jpg")) {
                        alt = alt.substring(0, alt.length() - 4);
                    }
                    Matcher matcher = IMG_SRC_REGEX.matcher(img.attr(CssSelector.ATTR_SRC));
                    works.add(new AdultVideo(StringUtils.isBlank(alt) ? null : alt, matcher.find() ? matcher.group() : null));
                }
            }
            Element next = document.selectFirst("div.pagination").selectFirst("a.next");
            if (next == null) {
                break;
            }
            document = getDocument(new URIBuilder(URI.create(next.attr(CssSelector.ATTR_HREF))), SnapshotStrategy.NEVER_UPDATE);
        }
        LocalDateTime release = LocalDateTime.parse(document.selectFirst("time.data-time").text(), Constants.STANDARD_DATE_TIME_FORMATTER);
        MidnightItem item = new MidnightItem(id, title, works, release);
        String keyword = document.selectFirst("meta[name=keywords]").attr(CssSelector.ATTR_CONTENT);
        item.setKeyword(StringUtils.isBlank(keyword) ? null : keyword);
        return item;
    }

    Map<Integer, String> getAllItems() throws HttpResponseException {
        Map<Integer, String> items = new HashMap<>(32);
        Document document = getDocument(builder0("/youyou"), SnapshotStrategy.ALWAYS_UPDATE);
        while (true) {
            Elements lis = document.selectFirst("div[role=main]").select(CssSelector.TAG_LI);
            for (Element li : lis) {
                Element a = li.selectFirst(CssSelector.TAG_A);
                String id = RegexUtils.matchesOrElseThrow(ACTRESS_ITEM_URL_REGEX, a.attr(CssSelector.ATTR_HREF)).group("id");
                items.put(Integer.parseInt(id), a.attr("title"));
            }

            Element next = document.selectFirst("div.pagination").selectFirst("a.next");
            if (next == null) {
                break;
            }
            document = getDocument(new URIBuilder(URI.create(next.attr(CssSelector.ATTR_HREF))), SnapshotStrategy.NEVER_UPDATE);
        }
        return items;
    }
}
