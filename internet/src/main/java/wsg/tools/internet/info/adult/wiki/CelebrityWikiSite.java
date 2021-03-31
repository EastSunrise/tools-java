package wsg.tools.internet.info.adult.wiki;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TriFunction;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.RequestBuilder;
import wsg.tools.internet.base.support.SnapshotStrategies;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.Scheme;
import wsg.tools.internet.common.StringResponseHandler;
import wsg.tools.internet.enums.BloodType;
import wsg.tools.internet.enums.Constellation;
import wsg.tools.internet.enums.Gender;
import wsg.tools.internet.enums.Nation;
import wsg.tools.internet.enums.Zodiac;
import wsg.tools.internet.info.adult.common.CupEnum;
import wsg.tools.internet.info.adult.entry.AdultEntryBuilder;
import wsg.tools.internet.info.adult.entry.FormalAdultEntry;

/**
 * @author Kingen
 * @see <a href="http://www.mrenbaike.net/">Wiki of Celebrities</a>
 * @since 2021/2/24
 */
@ConcreteSite
public final class CelebrityWikiSite extends BaseSite {

    private static final String TIP_MSG = "提示信息";
    private static final String VALUE_NULL = "暂无";
    private static final String SEPARATOR = "、";
    private static final String HOME_PAGE = "http://www.mrenbaike.net";
    private static final String NO_PERSON_IMG = "noperson.jpg";

    public CelebrityWikiSite() {
        super("Wiki of Celebrities", new BasicHttpSession(Scheme.HTTP, "mrenbaike.net"),
            new WikiResponseHandler());
    }

    /**
     * Returns the linked repository of albums of the given type.
     *
     * @see WikiAlbumType
     */
    @Nonnull
    public LinkedRepository<Integer, WikiAlbum> getAlbumRepository(@Nonnull WikiAlbumType type) {
        return Repositories.linked(id -> findAlbum(id, type), type.first());
    }

    /**
     * Retrieves the paged result of the indices under the given type.
     */
    @Nonnull
    public WikiPageResult findPage(@Nonnull WikiCelebrityType type, @Nonnull WikiPageReq request)
        throws NotFoundException, OtherResponseException {
        int current = request.getCurrent();
        String page = current == 0 ? "" : ("_" + (current + 1));
        RequestBuilder builder = builder0("/%s/index%s.html", type.getText(), page);
        Document document = getDocument(builder, SnapshotStrategies.never());

        Element box = document.selectFirst(".personbox");
        Elements lis = box.selectFirst(CssSelectors.TAG_UL).select(CssSelectors.TAG_LI);
        List<WikiCelebrityIndex> indices = lis.stream()
            .map(li -> li.selectFirst(CssSelectors.TAG_A))
            .map(a -> {
                String href = a.attr(CssSelectors.ATTR_HREF);
                Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.CELEBRITY_URL_REGEX, href);
                int id = Integer.parseInt(matcher.group("id"));
                return new WikiCelebrityIndex(id, a.text());
            }).collect(Collectors.toList());

        int total;
        Element a1 = box.selectFirst(".pagePg").selectFirst(".a1");
        if (a1 == null) {
            total = indices.size();
        } else {
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.TOTAL_REGEX, a1.text());
            total = Integer.parseInt(matcher.group("t"));
        }
        return new WikiPageResult(indices, request, total);
    }

    public WikiCelebrity findCelebrity(int id, @Nonnull WikiCelebrityType type)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = builder0("/%s/m%d/info.html", type.getText(), id);
        Document document = getDocument(builder, SnapshotStrategies.never());
        Elements ems = document.selectFirst("div.datacon").select(CssSelectors.TAG_EM);
        Map<String, String> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (Element em : ems) {
            String text = ((TextNode) em.nextSibling()).text().substring(1);
            if (StringUtils.isBlank(text) || VALUE_NULL.equals(text)) {
                continue;
            }
            String key = em.text().replace(" ", "");
            String preValue = map.get(key);
            if (preValue == null) {
                map.put(key, text);
                continue;
            }
            if (!preValue.equals(text)) {
                map.put(key, preValue + SEPARATOR + text);
            }
        }
        BasicInfo info = getBasicInfo(map);
        WikiCelebrity celebrity = initCelebrity(document,
            (id0, name, type0) -> new WikiCelebrity(id0, name, type0, info));
        Objects.requireNonNull(celebrity);
        Element ext = document.selectFirst("div.dataext");
        if (ext != null) {
            Elements elements = ext.select("h2.itit");
            Map<String, Element> extInfo = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
            for (Element ele : elements) {
                if (ele.hasText()) {
                    String key = ele.hasAttr("id") ? ele.id() : ele.text();
                    MapUtilsExt.putIfAbsent(extInfo, key, ele.nextElementSibling());
                }
            }
            setExtStringList(extInfo, celebrity, WikiCelebrity::setDescriptions, "人物介绍", "人物点评");
            setExtStringList(extInfo, celebrity, WikiCelebrity::setExperiences, "演艺阅历");
            setExtStringList(extInfo, celebrity, WikiCelebrity::setGroupLives, "团体生活");
            setExtStringList(extInfo, celebrity, WikiCelebrity::setAwards, "获奖记载");
            Element prompt = extInfo.remove("fanghao");
            if (prompt != null) {
                celebrity.setWorks(getWorks(prompt.nextElementSibling()));
            }
        }
        Set<WikiAlbumIndex> albums = getSimpleAlbums(type, id);
        if (CollectionUtils.isNotEmpty(albums)) {
            celebrity.setAlbums(albums);
        }
        return celebrity;
    }

    /**
     * Retrieves an album of the given identifier.
     */
    @Nonnull
    public WikiAlbum findAlbum(int id, @Nonnull WikiAlbumType type)
        throws NotFoundException, OtherResponseException {
        Document document = getDocument(builder0("/tuku/%s/%d.html", type.getText(), id),
            SnapshotStrategies.never());
        Element show = document.selectFirst("div.picshow");
        String title = show.selectFirst(CssSelectors.TAG_H1).text();
        String timeStr = ((TextNode) show.selectFirst(".info").childNode(0)).text();
        LocalDateTime updateTime = LocalDateTime.parse(timeStr, Constants.YYYY_MM_DD_HH_MM_SS);
        List<URL> images = show.selectFirst("#pictureurls").select(CssSelectors.TAG_IMG)
            .stream().map(img -> img.attr(CssSelectors.ATTR_REL)).map(NetUtils::createURL)
            .collect(Collectors.toList());
        WikiAlbum album = new WikiAlbum(id, type, title, updateTime, images);
        Element text = show.selectFirst("div.text");
        List<String> hrefs = text.select(">a").eachAttr(CssSelectors.ATTR_HREF).stream()
            .filter(s -> !HOME_PAGE.equals(s)).collect(Collectors.toList());
        if (!hrefs.isEmpty()) {
            List<Integer> related = new ArrayList<>();
            for (String s : hrefs) {
                Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.CELEBRITY_URL_REGEX, s);
                related.add(Integer.parseInt(matcher.group("id")));
            }
            album.setRelatedCelebrities(related);
        }
        Element tags = text.selectFirst("div.ptags");
        if (tags != null) {
            album.setTags(tags.select(CssSelectors.TAG_A).eachText());
        }
        Element next = show.selectFirst("div.next");
        String href = next.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
        if (href.contains(HOME_PAGE)) {
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.ALBUM_URL_REGEX, href);
            album.setNext(Integer.parseInt(matcher.group("id")));
        }
        return album;
    }

    /**
     * Retrieves an adult entry of the given identifier.
     */
    @Nonnull
    public WikiAdultEntry findAdultEntry(@Nonnull String id)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = builder0("/fanhao/%s.html", id);
        Document document = getDocument(builder, SnapshotStrategies.never());
        WikiSimpleCelebrity celebrity = initCelebrity(document, WikiSimpleCelebrity::new);
        Element div = document.selectFirst("div.fanhao");
        if (div.childNodeSize() == 1) {
            throw new NotFoundException(div.text());
        }
        String code = id.toUpperCase(Locale.ROOT);
        String src = div.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);
        URL cover = NetUtils.createURL(src);

        Map<String, String> info = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (Element em : div.select(CssSelectors.TAG_EM)) {
            String key = em.text().replace(" ", "");
            String value = ((TextNode) em.nextSibling()).text().substring(1);
            MapUtilsExt.putIfAbsent(info, key, value);
        }
        FormalAdultEntry entry = AdultEntryBuilder.builder(code, info)
            .validateCode().title().release().duration()
            .director().mosaic().producer().distributor()
            .series().tags(SEPARATOR).images(List.of(cover))
            .formal(SEPARATOR);
        return new WikiAdultEntry(entry, celebrity);
    }

    /**
     * Initializes basic properties of a celebrity.
     */
    @Nullable
    private <T extends WikiSimpleCelebrity> T initCelebrity(@Nonnull Document document,
        TriFunction<Integer, String, WikiCelebrityType, T> constructor) {
        Element current = document.selectFirst("a.current");
        if (current == null) {
            return null;
        }

        String href = current.attr(CssSelectors.ATTR_HREF);
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.CELEBRITY_URL_REGEX, href);
        int id = Integer.parseInt(matcher.group("id"));
        String name = document.selectFirst(".mx_name").text();
        String typeStr = matcher.group("t");
        WikiCelebrityType type = EnumUtilExt.valueOfText(WikiCelebrityType.class, typeStr, false);
        T t = constructor.apply(id, name, type);
        Element block01 = document.selectFirst("div.cm_block01");
        String image = block01.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);
        if (!image.endsWith(NO_PERSON_IMG)) {
            t.setCover(NetUtils.createURL(image));
        }
        Elements tags = block01.selectFirst("div.txt03").select(CssSelectors.TAG_A);
        if (!tags.isEmpty()) {
            t.setTags(tags.stream().map(Element::text).collect(Collectors.toList()));
        }
        return t;
    }

    @Nonnull
    private BasicInfo getBasicInfo(Map<String, String> map) {
        BasicInfo info = new BasicInfo();
        info.setGender(MapUtilsExt.getEnumOfTitle(map, Gender.class, false, "性别"));
        info.setFullName(MapUtilsExt.getString(map, "姓名"));
        setStringList(map, info, BasicInfo::setZhNames, "中文名");
        setStringList(map, info, BasicInfo::setJaNames, "日文名");
        setStringList(map, info, BasicInfo::setEnNames, "英文名");
        setStringList(map, info, BasicInfo::setAka, "别名");

        info.setZodiac(MapUtilsExt.getValue(map,
            text -> EnumUtilExt.valueOfTitle(Zodiac.class, text.substring(0, 1), false), "生肖"));
        info.setConstellation(MapUtilsExt.getEnumOfAka(map, Constellation.class, "星座"));
        setStringList(map, info, BasicInfo::setInterests, "兴趣", "爱好");

        info.setHeight(MapUtilsExt.getValue(map, text -> {
            Matcher matcher = Lazy.HEIGHT_REGEX.matcher(text);
            if (matcher.find()) {
                return (int) Math.round(Double.parseDouble(matcher.group("h")));
            }
            return null;
        }, "身高"));
        info.setWeight(MapUtilsExt.getValue(map, text -> {
            Matcher matcher = Lazy.WEIGHT_REGEX.matcher(text);
            if (matcher.find()) {
                return (int) Math.round(Double.parseDouble(matcher.group("w")));
            }
            return null;
        }, "体重"));
        info.setCup(MapUtilsExt.getValue(map, text -> {
            Matcher matcher = RegexUtils.findOrElseThrow(Lazy.CUP_REGEX, text.strip());
            return Enum.valueOf(CupEnum.class, matcher.group("c"));
        }, "罩杯"));
        info.setBloodType(MapUtilsExt.getValueIfMatched(map, Lazy.BLOOD_REGEX, matcher -> {
            String type = matcher.group("t");
            return "0".equals(type) ? BloodType.O
                : Enum.valueOf(BloodType.class, type.toUpperCase(Locale.ENGLISH));
        }, "血型"));

        info.setOccupations(MapUtilsExt.getStringList(map, "、，/., ", "职业"));
        info.setAgency(MapUtilsExt.getString(map, "经纪公司"));
        info.setFirm(MapUtilsExt.getString(map, "事务所"));
        info.setSchool(MapUtilsExt.getString(map, "毕业院校"));
        info.setBirthplace(MapUtilsExt.getString(map, "出生地"));
        info.setNation(MapUtilsExt.getEnumOfAka(map, Nation.class, "民族"));

        info.setOthers(new HashSet<>(map.keySet()));
        return info;
    }

    private <T> void setExtStringList(Map<String, Element> extInfo, T t,
        BiConsumer<T, List<String>> set, @Nonnull String... keys) {
        for (String key : keys) {
            Element nextEle = extInfo.remove(key);
            if (nextEle == null) {
                continue;
            }
            Elements ps = nextEle.select("p");
            List<String> values = ps.isEmpty() ? List.of(nextEle.text()) : ps.eachText();
            set.accept(t, values);
        }
    }

    private <T> void setStringList(Map<String, String> map, T t,
        BiConsumer<T, List<String>> set, String... keys) {
        List<String> values = MapUtilsExt.getStringList(map, SEPARATOR, keys);
        if (CollectionUtils.isNotEmpty(values)) {
            set.accept(t, values);
        }
    }

    private List<String> getWorks(Element icon) {
        List<String> works = new ArrayList<>();
        Element tbody = icon.selectFirst("tbody");
        if (tbody != null) {
            Elements trs = tbody.select(CssSelectors.TAG_TR);
            for (Element tr : trs) {
                String code = tr.selectFirst("td").text().strip();
                if (code.isBlank()) {
                    continue;
                }
                works.add(code);
            }
        }
        Elements labels = icon.select("label.born");
        if (!labels.isEmpty()) {
            works.addAll(labels.eachText());
        }
        return works.isEmpty() ? null : works;
    }

    private Set<WikiAlbumIndex> getSimpleAlbums(WikiCelebrityType type, int id)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = builder0("/%s/m%s/pic.html", type.getText(), id);
        Document document = getDocument(builder, SnapshotStrategies.never());
        Elements lis = document.selectFirst("#xiezhen").select(CssSelectors.TAG_LI);
        if (lis.isEmpty()) {
            return null;
        }
        Set<WikiAlbumIndex> albums = new HashSet<>();
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_A);
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.ALBUM_URL_REGEX, href);
            int albumId = Integer.parseInt(matcher.group("id"));
            String typeStr = matcher.group("t");
            WikiAlbumType albumType = EnumUtilExt.valueOfText(WikiAlbumType.class, typeStr, false);
            String title = a.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_ALT);
            albums.add(new WikiAlbumIndex(albumId, albumType, title));
        }
        return albums;
    }

    private static class Lazy {

        private static final Pattern CELEBRITY_URL_REGEX;
        private static final Pattern ALBUM_URL_REGEX;
        private static final Pattern HEIGHT_REGEX = Pattern
            .compile("(?<h>[12]\\d{2}(\\.\\d)?) ?(cm|CM|厘米|公分|㎝)");
        private static final Pattern WEIGHT_REGEX = Pattern
            .compile("(?<w>\\d{2,3}(\\.\\d)?) ?(kg|公斤|g|klg)", Pattern.CASE_INSENSITIVE);
        private static final Pattern CUP_REGEX = Pattern.compile("(?<c>[A-P])");
        private static final Pattern BLOOD_REGEX = Pattern
            .compile("(?<t>[ABO0]|AB)( ?型)?", Pattern.CASE_INSENSITIVE);
        private static final Pattern TOTAL_REGEX = Pattern.compile("(?<t>\\d+)条");

        static {
            String types = Arrays.stream(WikiCelebrityType.values()).map(TextSupplier::getText)
                .collect(Collectors.joining("|"));
            CELEBRITY_URL_REGEX = Pattern.compile(HOME_PAGE +
                "/(?<t>" + types + ")/m(?<id>\\d+)/((info|pic|news|comm)\\.html)?"
            );
            String albumTypes = Arrays.stream(WikiAlbumType.values()).map(TextSupplier::getText)
                .collect(Collectors.joining("|"));
            ALBUM_URL_REGEX = Pattern.compile(HOME_PAGE +
                "/tuku/(?<t>" + albumTypes + ")/(?<id>\\d+)(_(?<i>\\d+))?\\.html");
        }
    }

    private static class WikiResponseHandler extends StringResponseHandler {

        @Override
        protected String handleContent(String content) throws HttpResponseException {
            Document document = Jsoup.parse(content);
            if (TIP_MSG.equals(document.title())) {
                throw new HttpResponseException(HttpStatus.SC_NOT_FOUND,
                    document.selectFirst("div.content").text());
            }
            return content;
        }
    }
}
