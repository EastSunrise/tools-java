package wsg.tools.internet.info.adult.wiki;

import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
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
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TriFunction;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.Repositories;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.IntIndicesRepository;
import wsg.tools.internet.base.intf.LinkedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.Scheme;
import wsg.tools.internet.common.StringResponseHandler;
import wsg.tools.internet.enums.BloodType;
import wsg.tools.internet.enums.Constellation;
import wsg.tools.internet.enums.Gender;
import wsg.tools.internet.enums.Nation;
import wsg.tools.internet.enums.Zodiac;
import wsg.tools.internet.info.adult.common.AdultEntry;
import wsg.tools.internet.info.adult.common.AdultEntryBuilder;
import wsg.tools.internet.info.adult.common.CupEnum;

/**
 * @author Kingen
 * @see <a href="http://www.mrenbaike.net/">Wiki of Celebrities</a>
 * @since 2021/2/24
 */
public final class CelebrityWikiSite extends BaseSite {

    private static final Set<Integer> NOT_FOUNDS = Set.of(
        1757, 1568, 1539, 821
    );
    private static final String TIP_MSG = "提示信息";
    private static final String VALUE_NULL = "暂无";
    private static final String SEPARATOR = "、";
    private static final String FAKE_FEMALE = "伪娘";
    private static final Pattern CELEBRITY_HREF_REGEX;
    private static final Pattern ALBUM_HREF_REGEX;
    private static final int CENTIMETERS_PER_METER = 100;
    private static final String HOME_PAGE = "http://www.mrenbaike.net";
    private static final String NO_PERSON_IMG = "noperson.jpg";
    private static final Pattern TOTAL_REGEX = Pattern.compile("(?<t>\\d+)条");
    private static final Pattern LOCAL_DATE_REGEX = Pattern
        .compile("(?<y>(18|19|20)\\d{2})([年./-])"
            + "(?<m>0?[1-9]|1[012])(月|\\3)"
            + "(?<d>0?[1-9]|[12][0-9]|3[01])(?!\\d)");
    private static final Pattern YEAR_MONTH_REGEX =
        Pattern.compile("(?<y>(18|19|20)\\d{2})[年.-](?<m>0?[1-9]|1[012])(月[^\\d]*)?");
    private static final Pattern YEAR_REGEX = Pattern.compile("(?<y>(18|19|20)\\d{2})(年[^\\d]*)?");
    private static final Pattern MONTH_DAY_REGEX =
        Pattern.compile("(?<m>0?[1-9]|1[012])[月./-](?<d>0?[1-9]|[12][0-9]|3[01])([日号].*)?");
    private static final Pattern HEIGHT_REGEX =
        Pattern.compile(
            "(?<h>[12]\\d{2}(\\.\\d)?|[12]\\.\\d{2}) ?(cm|CM|厘米|公分|m|㎝)?([(（][^()]+[）)])?");
    private static final Pattern WEIGHT_REGEX =
        Pattern.compile("(?<w>\\d{2,3}(\\.\\d)?) ?(kg|公斤|g|klg)([(（][^()]+[）)])?",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern CUP_REGEX = Pattern.compile("(?<c>[A-P])(罩?杯?|-\\d{2,3})");
    private static final Pattern BLOOD_REGEX = Pattern
        .compile("(?<t>[ABO0]|AB)( ?型)?", Pattern.CASE_INSENSITIVE);

    static {
        String celebrityTypes =
            Arrays.stream(WikiCelebrityType.values()).map(TextSupplier::getText)
                .collect(Collectors.joining("|"));
        CELEBRITY_HREF_REGEX = Pattern.compile(
            "http://www\\.mrenbaike\\.net/(?<t>" + celebrityTypes
                + ")/m(?<id>\\d+)/((info|pic|news|comm)\\.html)?");
        String albumTypes =
            Arrays.stream(WikiAlbumType.values()).map(TextSupplier::getText)
                .collect(Collectors.joining("|"));
        ALBUM_HREF_REGEX = Pattern
            .compile("http://www\\.mrenbaike\\.net/tuku/(?<t>" + albumTypes
                + ")/(?<id>\\d+)(_(?<i>\\d+))?\\.html");
    }

    public CelebrityWikiSite() {
        super("Wiki of Celebrities", new BasicHttpSession(Scheme.HTTP, "mrenbaike.net"),
            new WikiResponseHandler());
    }

    /**
     * Returns the repository of the celebrities of the given type.
     */
    public IntIndicesRepository<WikiCelebrity> getCelebrityRepository(
        @Nonnull WikiCelebrityType type) throws HttpResponseException {
        List<Integer> ids = new ArrayList<>();
        WikiPageRequest request = new WikiPageRequest(0);
        while (true) {
            WikiPageResult result = findAllCelebrityIndices(type, request);
            result.getContent().stream().map(WikiCelebrityIndex::getId)
                .filter(id -> !NOT_FOUNDS.contains(id)).forEach(ids::add);
            if (!result.hasNext()) {
                break;
            }
            request = result.nextPageRequest();
        }
        return Repositories.intIndices(id -> findCelebrity(id, type), ids);
    }

    /**
     * Finds the paged result of the indices under the given type.
     */
    public WikiPageResult findAllCelebrityIndices(
        @Nonnull WikiCelebrityType type, @Nonnull WikiPageRequest request)
        throws HttpResponseException {
        int current = request.getCurrent();
        String page = current == 0 ? "" : ("_" + (current + 1));
        RequestBuilder builder = builder0("/%s/index%s.html", type.getText(), page);
        Document document = getDocument(builder, SnapshotStrategy.never());

        Element box = document.selectFirst(".personbox");
        Elements lis = box.selectFirst(CssSelectors.TAG_UL).select(CssSelectors.TAG_LI);
        Pattern urlRegex = Pattern
            .compile("http://www\\.mrenbaike\\.net/" + type.getText() + "/m(?<id>\\d+)/");
        List<WikiCelebrityIndex> indices = lis.stream()
            .map(li -> li.selectFirst(CssSelectors.TAG_A))
            .map(a -> {
                String href = a.attr(CssSelectors.ATTR_HREF);
                Matcher matcher = RegexUtils.matchesOrElseThrow(urlRegex, href);
                int id = Integer.parseInt(matcher.group("id"));
                return new WikiCelebrityIndex(id, a.text());
            }).collect(Collectors.toList());

        int total;
        Element a1 = box.selectFirst(".pagePg").selectFirst(".a1");
        if (a1 == null) {
            total = indices.size();
        } else {
            Matcher matcher = RegexUtils.matchesOrElseThrow(TOTAL_REGEX, a1.text());
            total = Integer.parseInt(matcher.group("t"));
        }
        return new WikiPageResult(indices, request, total);
    }

    /**
     * Returns the linked repository of albums of the given type.
     *
     * @see WikiAlbumType
     */
    public LinkedRepository<Integer, WikiAlbum> getAlbumRepository(WikiAlbumType type) {
        return Repositories.linked(id -> findAlbum(id, type), type.first());
    }

    /**
     * Returns the repository of adult entries which are attached to celebrities.
     *
     * @see WikiCelebrity#getWorks()
     */
    public Repository<String, WikiAdultEntry> getEntryRepository() {
        return this::findAdultEntry;
    }

    public WikiCelebrity findCelebrity(int id, @Nonnull WikiCelebrityType type)
        throws HttpResponseException {
        RequestBuilder builder = builder0("/%s/m%d/info.html", type.getText(), id);
        Document document = getDocument(builder, SnapshotStrategy.never());
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

    public WikiAlbum findAlbum(int id, WikiAlbumType type) throws HttpResponseException {
        Document document = getDocument(builder0("/tuku/%s/%d.html", type.getText(), id),
            SnapshotStrategy.never());
        Element show = document.selectFirst("div.picshow");
        String title = show.selectFirst(CssSelectors.TAG_H1).text();
        LocalDateTime updateTime = LocalDateTime
            .parse(((TextNode) show.selectFirst(".info").childNode(0)).text(),
                Constants.YYYY_MM_DD_HH_MM_SS);
        List<String> images = show.selectFirst("#pictureurls").select(CssSelectors.TAG_IMG)
            .eachAttr(CssSelectors.ATTR_REL);
        WikiAlbum album = new WikiAlbum(id, type, title, updateTime, images);
        Element text = show.selectFirst("div.text");
        List<String> hrefs = text.select(">a").eachAttr(CssSelectors.ATTR_HREF).stream()
            .filter(s -> !HOME_PAGE.equals(s)).collect(Collectors.toList());
        if (!hrefs.isEmpty()) {
            album.setRelatedCelebrities(
                hrefs.stream()
                    .map(s -> RegexUtils.matchesOrElseThrow(CELEBRITY_HREF_REGEX, s).group("id"))
                    .map(Integer::parseInt).collect(Collectors.toList()));
        }
        Element tags = text.selectFirst("div.ptags");
        if (tags != null) {
            album.setTags(tags.select(CssSelectors.TAG_A).eachText());
        }
        Element next = show.selectFirst("div.next");
        String href = next.selectFirst(CssSelectors.TAG_A).attr(CssSelectors.ATTR_HREF);
        if (href.contains(HOME_PAGE)) {
            Matcher matcher = RegexUtils.matchesOrElseThrow(ALBUM_HREF_REGEX, href);
            album.setNext(Integer.parseInt(matcher.group("id")));
        }
        return album;
    }

    public WikiAdultEntry findAdultEntry(@Nonnull String id) throws HttpResponseException {
        RequestBuilder builder = builder0("/fanhao/%s.html", id);
        Document document = getDocument(builder, SnapshotStrategy.never());
        WikiSimpleCelebrity celebrity = initCelebrity(document, WikiSimpleCelebrity::new);
        Element div = document.selectFirst("div.fanhao");
        if (div.childNodeSize() == 1) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, div.text());
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
        AdultEntry entry = AdultEntryBuilder.formal(info, code, SEPARATOR)
            .validateCode().title().release().duration()
            .director().mosaic().producer().distributor()
            .series().tags(SEPARATOR).images(List.of(cover)).build();
        return new WikiAdultEntry(entry, celebrity);
    }

    /**
     * Initializes basic properties of a celebrity.
     */
    private <T extends WikiSimpleCelebrity> T initCelebrity(Document document,
        TriFunction<Integer, String, WikiCelebrityType, T> constructor) {
        Element current = document.selectFirst("a.current");
        if (current == null) {
            return null;
        }

        String href = current.attr(CssSelectors.ATTR_HREF);
        Matcher matcher = RegexUtils.matchesOrElseThrow(CELEBRITY_HREF_REGEX, href);
        int id = Integer.parseInt(matcher.group("id"));
        String name = document.selectFirst(".mx_name").text();
        WikiCelebrityType type = EnumUtilExt
            .valueOfText(matcher.group("t"), WikiCelebrityType.class, false);
        T t = constructor.apply(id, name, type);
        Element block01 = document.selectFirst("div.cm_block01");
        String image = block01.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);
        if (!image.endsWith(NO_PERSON_IMG)) {
            t.setCover(image);
        }
        Elements tags = block01.selectFirst("div.txt03").select(CssSelectors.TAG_A);
        if (!tags.isEmpty()) {
            t.setTags(tags.stream().map(Element::text).collect(Collectors.toList()));
        }
        return t;
    }

    private BasicInfo getBasicInfo(Map<String, String> map) {
        BasicInfo info = new BasicInfo();
        info.setGender(MapUtilsExt.getValue(map, s -> {
            if (FAKE_FEMALE.equals(s)) {
                return Gender.MALE;
            }
            return EnumUtilExt.valueOfTitle(s, Gender.class, false);
        }, "性别"));
        info.setBirthday(MapUtilsExt.getValue(map, this::parseBirthday, "出生日期"));
        info.setFullName(MapUtilsExt.getString(map, "姓名"));
        setStringList(map, info, BasicInfo::setZhNames, "中文名");
        setStringList(map, info, BasicInfo::setJaNames, "日文名");
        setStringList(map, info, BasicInfo::setEnNames, "英文名");
        setStringList(map, info, BasicInfo::setAka, "别名");

        info.setZodiac(MapUtilsExt.getValue(map,
            text -> EnumUtilExt.valueOfTitle(text.substring(0, 1), Zodiac.class, false), "生肖"));
        info.setConstellation(
            MapUtilsExt.getValue(map, text -> deserializeAka(text, Constellation.class), "星座"));
        setStringList(map, info, BasicInfo::setInterests, "兴趣", "爱好");

        info.setHeight(MapUtilsExt.getValueIfMatched(map, HEIGHT_REGEX, matcher -> {
            double height = Double.parseDouble(matcher.group("h"));
            if (height < CENTIMETERS_PER_METER) {
                height *= CENTIMETERS_PER_METER;
            }
            return (int) Math.round(height);
        }, "身高"));
        info.setWeight(MapUtilsExt.getValueIfMatched(map, WEIGHT_REGEX,
            matcher -> (int) Math.round(Double.parseDouble(matcher.group("w"))), "体重"));
        info.setCup(MapUtilsExt.getValue(map,
            text -> Enum.valueOf(CupEnum.class,
                RegexUtils.matchesOrElseThrow(CUP_REGEX, text.strip()).group("c")),
            "罩杯"));
        info.setFigure(MapUtilsExt.getValue(map, Figure::of, "三围"));
        info.setBloodType(MapUtilsExt.getValueIfMatched(map, BLOOD_REGEX, matcher -> {
            String type = matcher.group("t");
            return "0".equals(type) ? BloodType.O
                : Enum.valueOf(BloodType.class, type.toUpperCase(Locale.ENGLISH));
        }, "血型"));

        List<String> occupations = MapUtilsExt.getStringList(map, "、，/., ", "职业");
        if (occupations != null) {
            info.setOccupations(occupations);
        }
        info.setStart(
            MapUtilsExt.getValue(map, text -> parsePartDate(text.replace(" ", "")), "出道时间"));
        info.setRetire(MapUtilsExt.getValue(map, this::parsePartDate, "隐退时间"));
        info.setAgency(MapUtilsExt.getString(map, "经纪公司"));
        info.setFirm(MapUtilsExt.getString(map, "事务所"));
        info.setSchool(MapUtilsExt.getString(map, "毕业院校"));
        info.setBirthplace(MapUtilsExt.getString(map, "出生地"));
        info.setNation(MapUtilsExt.getValue(map, text -> deserializeAka(text, Nation.class), "民族"));

        info.setOthers(new HashSet<>(map.keySet()));
        return info;
    }

    private TemporalAccessor parseBirthday(String text) {
        text = text.replace(" ", "");
        Temporal temporal = parsePartDate(text);
        if (temporal != null) {
            return temporal;
        }
        Matcher matcher = MONTH_DAY_REGEX.matcher(text);
        if (matcher.matches()) {
            return MonthDay
                .of(Integer.parseInt(matcher.group("m")), Integer.parseInt(matcher.group("d")));
        }
        return null;
    }

    private <T extends Enum<T> & AkaPredicate<String>> T
    deserializeAka(String text, Class<T> clazz) {
        try {
            return EnumUtilExt.deserializeAka(text, clazz);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Temporal parsePartDate(String text) {
        Matcher ymd = LOCAL_DATE_REGEX.matcher(text);
        if (ymd.find()) {
            int year = Integer.parseInt(ymd.group("y"));
            int month = Integer.parseInt(ymd.group("m"));
            int day = Integer.parseInt(ymd.group("d"));
            try {
                return LocalDate.of(year, month, day);
            } catch (DateTimeException e) {
                return null;
            }
        }
        Matcher ym = YEAR_MONTH_REGEX.matcher(text);
        if (ym.matches()) {
            return YearMonth.of(Integer.parseInt(ym.group("y")), Integer.parseInt(ym.group("m")));
        }
        Matcher y = YEAR_REGEX.matcher(text);
        if (y.matches()) {
            return Year.of(Integer.parseInt(y.group("y")));
        }
        return null;
    }

    private <T> void setExtStringList(Map<String, Element> extInfo, T t,
        BiConsumer<T, List<String>> set, String... keys) {
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
        throws HttpResponseException {
        RequestBuilder builder = builder0("/%s/m%s/pic.html", type.getText(), id);
        Document document = getDocument(builder, SnapshotStrategy.never());
        Elements lis = document.selectFirst("#xiezhen").select(CssSelectors.TAG_LI);
        if (lis.isEmpty()) {
            return null;
        }
        Set<WikiAlbumIndex> albums = new HashSet<>();
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_A);
            Matcher matcher = RegexUtils
                .matchesOrElseThrow(ALBUM_HREF_REGEX, a.attr(CssSelectors.ATTR_HREF));
            int albumId = Integer.parseInt(matcher.group("id"));
            WikiAlbumType albumType = EnumUtilExt
                .valueOfText(matcher.group("t"), WikiAlbumType.class, false);
            String title = a.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_ALT);
            albums.add(new WikiAlbumIndex(albumId, albumType, title));
        }
        return albums;
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
