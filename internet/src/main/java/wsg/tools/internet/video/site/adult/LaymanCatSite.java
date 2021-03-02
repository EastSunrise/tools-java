package wsg.tools.internet.video.site.adult;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.BasicHttpSession;
import wsg.tools.internet.base.RecordIterator;
import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.IterableRepositoryImpl;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.common.Scheme;
import wsg.tools.internet.common.WithoutNextDocument;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kingen
 * @see <a href="http://www.surenmao.com/">Layman Cat</a>
 * @since 2021/2/28
 */
public class LaymanCatSite extends BaseSite implements Repository<String, LaymanCatAdultVideo>, IterableRepository<LaymanCatAdultVideo> {

    private static final Pattern HREF_REGEX = Pattern.compile("http://www\\.surenmao\\.com/(?<c>[0-9a-z-]+)/");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final Pattern DURATION_REGEX = Pattern.compile("(?<d>\\d+)(min|分)");
    private static final String FIRST_KEY = "収録時間";
    private static LaymanCatSite instance;

    private final IterableRepository<LaymanCatAdultVideo> repository = new IterableRepositoryImpl<>(this, "200gana-1829");

    private LaymanCatSite() {
        super("Layman Cat", new BasicHttpSession(Scheme.HTTP, "surenmao.com"));
    }

    public synchronized static LaymanCatSite getInstance() {
        if (instance == null) {
            instance = new LaymanCatSite();
        }
        return instance;
    }

    @Override
    public RecordIterator<LaymanCatAdultVideo> iterator() throws HttpResponseException {
        return repository.iterator();
    }

    @Override
    public LaymanCatAdultVideo findById(@Nonnull String code) throws HttpResponseException {
        Document document = getDocument(builder0("/%s/", code), new WithoutNextDocument<>(this::getNext));
        Element main = document.selectFirst("#main");
        String title = main.selectFirst("h1.entry-title").text();
        String cover = main.selectFirst("img.size-full").attr("src");
        LocalDateTime published = LocalDateTime.parse(main.selectFirst("time.published").attr("datetime"), DATETIME_FORMATTER);
        LocalDateTime updated = LocalDateTime.parse(main.selectFirst("time.updated").attr("datetime"), DATETIME_FORMATTER);
        String author = main.selectFirst("span.author").text();
        LaymanCatAdultVideo video = new LaymanCatAdultVideo(title, cover, published, updated, author);

        video.setNext(getNext(document));
        Elements children = main.selectFirst("div.entry-content").children();
        Map<String, List<Element>> map = children.stream().collect(Collectors.groupingBy(Element::tagName));
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

    private String getNext(Document document) {
        Element next = document.selectFirst("div.nav-next");
        if (next == null) {
            return null;
        }
        String href = next.selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF);
        return RegexUtils.matchesOrElseThrow(HREF_REGEX, href).group("c");
    }

    private void setInfo(LaymanCatAdultVideo video, List<String> lines) {
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
    }

    private <T> T getValue(Map<String, String> map, String key, Function<String, T> function) {
        String value = map.remove(key);
        return value == null ? null : function.apply(value);
    }
}
