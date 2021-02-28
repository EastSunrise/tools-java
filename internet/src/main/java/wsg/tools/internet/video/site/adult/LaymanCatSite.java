package wsg.tools.internet.video.site.adult;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseRepository;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.CssSelector;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.enums.SchemeEnum;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Videos on the site are related like a list with sibling videos found by {@link LaymanAdultVideo#getPrevious()}
 * and {@link LaymanAdultVideo#getNext()}.
 *
 * @author Kingen
 * @see <a href="http://www.surenmao.com/">Layman Cat</a>
 * @since 2021/2/28
 */
public class LaymanCatSite extends BaseSite implements BaseRepository<String, LaymanAdultVideo> {

    private static final Pattern HREF_REGEX = Pattern.compile("http://www\\.surenmao\\.com/(?<c>[0-9a-z-]+)/");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final Pattern DURATION_REGEX = Pattern.compile("(?<d>\\d+)(min|分)");
    private static final String FIRST_KEY = "収録時間";
    private static final String FIRST_CODE = "200gana-1829";
    private static LaymanCatSite instance;

    private LaymanCatSite() {
        super("Layman Cat", SchemeEnum.HTTP, "surenmao.com");
    }

    public static LaymanCatSite getInstance() {
        if (instance == null) {
            instance = new LaymanCatSite();
        }
        return instance;
    }

    @Override
    public List<LaymanAdultVideo> findAll() throws HttpResponseException {
        List<LaymanAdultVideo> videos = new ArrayList<>();
        LaymanAdultVideo video = findById(FIRST_CODE);
        while (true) {
            videos.add(video);
            if (!video.hasNext()) {
                break;
            }
            video = findById(video.getNext());
        }
        return videos;
    }

    @Override
    public LaymanAdultVideo findById(String code) throws HttpResponseException {
        Document document = getDocument(builder0("/%s/", code), SnapshotStrategy.NEVER_UPDATE);
        Element main = document.selectFirst("#main");
        String title = main.selectFirst("h1.entry-title").text();
        String cover = main.selectFirst("img.size-full").attr("src");
        LocalDateTime published = LocalDateTime.parse(main.selectFirst("time.published").attr("datetime"), DATETIME_FORMATTER);
        LocalDateTime updated = LocalDateTime.parse(main.selectFirst("time.updated").attr("datetime"), DATETIME_FORMATTER);
        String author = main.selectFirst("span.author").text();
        LaymanAdultVideo video = new LaymanAdultVideo(title, cover, published, updated, author);

        Element prev = document.selectFirst("div.nav-previous");
        if (prev != null) {
            String href = prev.selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF);
            video.setPrevious(RegexUtils.matchesOrElseThrow(HREF_REGEX, href).group("c"));
        }
        Element next = document.selectFirst("div.nav-next");
        if (next != null) {
            String href = next.selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF);
            video.setNext(RegexUtils.matchesOrElseThrow(HREF_REGEX, href).group("c"));
        }

        Elements children = main.selectFirst("div.entry-content").children();
        Map<String, List<Element>> map = children.stream().collect(Collectors.groupingBy(Element::tagName));
        video.setContent(map);
        if (map.size() == 1) {
            Element current = children.first();
            if (current.childNodeSize() == 1) {
                current = current.nextElementSibling();
            }
            StringBuilder description = new StringBuilder(current.text());
            current = current.nextElementSibling();
            if (current == null) {
                video.setDescription(description.toString());
                return video;
            }
            while (current.childNodeSize() == 1) {
                description.append(current.text());
                current = current.nextElementSibling();
            }
            video.setDescription(description.toString());
            setInfo(video, current.childNodes().stream().filter(n -> n instanceof TextNode).map(n -> (TextNode) n).map(TextNode::text).collect(Collectors.toList()));
            return video;
        }
        if (map.containsKey(CssSelector.TAG_DIV)) {
            video.setDescription(children.get(1).text());
            setInfo(video, map.get(CssSelector.TAG_DIV).stream().map(Element::text).collect(Collectors.toList()));
        }
        return video;
    }

    private void setInfo(LaymanAdultVideo video, List<String> lines) {
        Map<String, String> info = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        Iterator<String> iterator = lines.iterator();
        String[] parts = StringUtils.split(StringUtils.stripStart(iterator.next(), " ・"), ":：", 2);
        if (FIRST_KEY.equals(parts[0])) {
            parts[1] = parts[1].split("・")[0];
        }
        info.put(parts[0], parts[1].strip());
        while (iterator.hasNext()) {
            String[] kv = StringUtils.split(StringUtils.stripStart(iterator.next(), " ・"), ":：", 2);
            if (kv.length == 2) {
                info.put(kv[0], kv[1].strip());
            }
        }
        String title = getValue(info, "出演", Function.identity());
        video.setTitle(title != null ? title : getValue(info, "出演者", Function.identity()));
        video.setDuration(getValue(info, "収録時間", s -> Duration.ofMinutes(Integer.parseInt(RegexUtils.matchesOrElseThrow(DURATION_REGEX, s).group("d")))));
        video.setReleased(getValue(info, "商品発売日", text -> false));
        video.setPost(getValue(info, "公開日", text -> LocalDate.parse(text.substring(3), DATE_FORMATTER)));
        video.setRelease(getValue(info, "配信開始日", text -> LocalDate.parse(text, DATE_FORMATTER)));
        video.setSeries(getValue(info, "シリーズ", Function.identity()));
        video.setDistributor(getValue(info, "レーベル", Function.identity()));
        video.setProducer(getValue(info, "メーカー", Function.identity()));
        video.setTags(getValue(info, "ジャンル", text -> Arrays.asList(text.split(" "))));
        video.setInfo(info);
    }

    private <T> T getValue(Map<String, String> map, String key, Function<String, T> function) {
        String value = map.remove(key);
        return value == null ? null : function.apply(value);
    }
}
