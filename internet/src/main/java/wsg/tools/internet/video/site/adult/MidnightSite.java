package wsg.tools.internet.video.site.adult;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.BasicHttpSession;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.IterableRepositoryImpl;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.common.WithoutNextDocument;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://www.shenyequ.com/">Midnight Zone</a>
 * @since 2021/2/22
 */
public class MidnightSite extends BaseSite {

    private static final String HASH = "#";
    private static final Pattern ACTRESS_URL_REGEX = Pattern.compile("https://www\\.shenyequ\\.com/youyou/(?<id>\\d+).html");
    private static final Pattern IMG_SRC_REGEX = Pattern.compile("https://syqpic\\.hantangrx\\.com/d/file/\\d{4}-\\d{2}-\\d{2}/[a-z0-9]{32}\\.jpg");
    private static MidnightSite instance;

    @Getter
    private final IterableRepository<MidnightAdultActress> actressRepository = new IterableRepositoryImpl<>(this::getAdultActress, 53);

    private MidnightSite() {
        super("Midnight", new BasicHttpSession("shenyequ.com"));
    }

    public synchronized static MidnightSite getInstance() {
        if (instance == null) {
            instance = new MidnightSite();
        }
        return instance;
    }

    public MidnightAdultActress getAdultActress(int id) throws HttpResponseException {
        Document document = getDocument(builder0("/youyou/%d.html", id), new WithoutNextDocument<>(this::getNext));
        String title = document.selectFirst("h1.title").text();
        LocalDateTime release = LocalDateTime.parse(document.selectFirst("time.data-time").text(), Constants.STANDARD_DATE_TIME_FORMATTER);
        MidnightAdultActress actress = new MidnightAdultActress(id, title, release);
        actress.setNext(getNext(document));

        List<MidnightAdultVideo> works = new ArrayList<>();
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
                    works.add(new MidnightAdultVideo(StringUtils.isBlank(alt) ? null : alt, matcher.find() ? matcher.group() : null));
                }
            }
            Element next = document.selectFirst("div.pagination").selectFirst("a.next");
            if (next == null) {
                break;
            }
            document = getDocument(builder0(URI.create(next.attr(CssSelector.ATTR_HREF)).getPath()), SnapshotStrategy.never());
        }
        actress.setWorks(works);
        String keyword = document.selectFirst("meta[name=keywords]").attr(CssSelector.ATTR_CONTENT);
        actress.setKeyword(StringUtils.isBlank(keyword) ? null : keyword);
        return actress;
    }

    private Integer getNext(Document document) {
        String next = document.selectFirst("a.next-post").attr(CssSelector.ATTR_HREF);
        if (HASH.equals(next)) {
            return null;
        }
        return Integer.parseInt(RegexUtils.matchesOrElseThrow(ACTRESS_URL_REGEX, next).group("id"));
    }
}
