package wsg.tools.internet.info.adult.mr;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Duration;
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TriFunction;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.IntRangeIterableRepositoryImpl;
import wsg.tools.internet.base.impl.LinkedRepositoryImpl;
import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.Scheme;
import wsg.tools.internet.enums.BloodType;
import wsg.tools.internet.enums.Constellation;
import wsg.tools.internet.enums.Gender;
import wsg.tools.internet.enums.Language;
import wsg.tools.internet.enums.Nation;
import wsg.tools.internet.enums.Region;
import wsg.tools.internet.enums.Zodiac;
import wsg.tools.internet.info.adult.AdultEntry;
import wsg.tools.internet.info.adult.AdultEntryUtils;
import wsg.tools.internet.info.adult.common.CupEnum;
import wsg.tools.internet.info.adult.common.Mosaic;

/**
 * @author Kingen
 * @see <a href="http://www.mrenbaike.net/">Wiki of Celebrities</a>
 * @since 2021/2/24
 */
public final class CelebrityWikiSite extends BaseSite {

    private static final String TIP_MSG = "提示信息";
    private static final String VALUE_NULL = "暂无";
    private static final String SEPARATOR = "、";
    private static final String FAKE_FEMALE = "伪娘";
    private static final Pattern CELEBRITY_HREF_REGEX;
    private static final Pattern ALBUM_HREF_REGEX;
    private static final int CENTIMETERS_PER_METER = 100;
    private static final String HOME_PAGE = "http://www.mrenbaike.net";
    private static final String LAST_PAGE = "javascript:alert('最后一页');";
    private static final String NO_PERSON_IMG = "http://www.mrenbaike.net/statics/images/noperson.jpg";
    private static final Pattern LOCAL_DATE_REGEX = Pattern
        .compile(
            "(?<y>(18|19|20)\\d{2})([年./-])(?<m>0?[1-9]|1[012])(月|\\3)(?<d>0?[1-9]|[12][0-9]|3[01])(?!\\d)");
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
    private static final Pattern FIGURE_REGEX = Pattern.compile(
        "(?<b>\\d{2,3})(?<c>[A-L])?([^\\d]+)(?<w>\\d{2})\\3(?<h>\\d{2,3})[^\\d]{0,4}",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern FIGURE_REGEX2 = Pattern.compile(
        "B?([.:]?)(?<b>\\d{2,3})([(（]?(?<c>[A-L])[）)]?)?([^\\d]*)W\\1(?<w>\\d{2})\\5H\\1(?<h>\\d{2,3})[^\\d]{0,3}",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern CUP_REGEX = Pattern.compile("(?<c>[A-P])(罩?杯?|-\\d{2,3})");
    private static final Pattern BLOOD_REGEX = Pattern
        .compile("(?<t>[ABO0]|AB)( ?型)?", Pattern.CASE_INSENSITIVE);
    private static final String BLOCKED_TITLE = "****名人百科网根据服务器要求进行屏蔽****";
    private static final Pattern DURATION_REGEX = Pattern.compile("(?<d>\\d{2,4}) ?(分钟|分|钟)");
    private static final Pattern RELEASE_REGEX = Pattern
        .compile("(?<y>\\d{4})([-/])(?<m>\\d{1,2})\\2(?<d>\\d{1,2})");

    static {
        String celebrityTypes =
            Arrays.stream(CelebrityType.values()).map(TextSupplier::getText)
                .collect(Collectors.joining("|"));
        CELEBRITY_HREF_REGEX = Pattern.compile(
            "http://www\\.mrenbaike\\.net/(?<t>" + celebrityTypes
                + ")/m(?<id>\\d+)/((info|pic|news|comm)\\.html)?");
        String albumTypes =
            Arrays.stream(AlbumType.values()).map(TextSupplier::getText)
                .collect(Collectors.joining("|"));
        ALBUM_HREF_REGEX = Pattern
            .compile("http://www\\.mrenbaike\\.net/tuku/(?<t>" + albumTypes
                + ")/(?<id>\\d+)(_(?<i>\\d+))?\\.html");
    }

    private CelebrityWikiSite() {
        super("Wiki of Celebrities", new BasicHttpSession(Scheme.HTTP, "mrenbaike.net"),
            new AbstractResponseHandler<>() {
                @Override
                public String handleEntity(HttpEntity entity) throws IOException {
                    String content = EntityUtils.toString(entity);
                    Document document = Jsoup.parse(content);
                    if (TIP_MSG.equals(document.title())) {
                        throw new HttpResponseException(HttpStatus.SC_NOT_FOUND,
                            document.selectFirst("div.content").text());
                    }
                    return content;
                }
            });
    }

    /**
     * Obtains the repository of celebrities based on identifiers. May that some records are not
     * found.
     */
    public IterableRepository<Celebrity> getCelebrityRepository() {
        return new IntRangeIterableRepositoryImpl<>(this::findCelebrity, 9601);
    }

    public IterableRepository<CelebrityAlbum> getAlbumRepository(AlbumType type) {
        return new LinkedRepositoryImpl<>(id -> findAlbum(id, type), type.first());
    }

    public Repository<String, CelebrityAdultEntry> getEntryRepository() {
        return this::findAdultEntry;
    }

    public Celebrity findCelebrity(int id) throws HttpResponseException {
        Document document = getDocument(builder0("/yule/m%d/info.html", id),
            SnapshotStrategy.never());
        Elements ems = document.selectFirst("div.datacon").select("em");
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
        Celebrity celebrity = initCelebrity(document,
            (i, name, type) -> new Celebrity(i, name, type, info));
        Element ext = document.selectFirst("div.dataext");
        if (ext != null) {
            Map<String, Element> extInfo = ext.select("h2.itit").stream().filter(Element::hasText)
                .collect(Collectors
                    .toMap(e -> e.hasAttr("id") ? e.id() : e.text(), Element::nextElementSibling));
            List<String> desc = getExtStringList(extInfo, "人物介绍");
            celebrity.setDescriptions(desc != null ? desc : getExtStringList(extInfo, "人物点评"));
            celebrity.setExperiences(getExtStringList(extInfo, "演艺阅历"));
            celebrity.setGroupLives(getExtStringList(extInfo, "团体生活"));
            celebrity.setAwards(getExtStringList(extInfo, "获奖记载"));
            Element prompt = extInfo.remove("fanghao");
            if (prompt != null) {
                celebrity.setWorks(getWorks(prompt.nextElementSibling()));
            }
            extInfo.remove("hotfanghao");
        }
        celebrity.setAlbums(
            getAlbums(getDocument(builder0("/yule/m%s/pic.html", id), SnapshotStrategy.never())));
        return celebrity;
    }

    public CelebrityAlbum findAlbum(int id, AlbumType type) throws HttpResponseException {
        Document document = getDocument(builder0("/tuku/%s/%d.html", type.getText(), id),
            SnapshotStrategy.never());
        Element show = document.selectFirst("div.picshow");
        String title = show.selectFirst(CssSelectors.TAG_H1).text();
        LocalDateTime updateTime = LocalDateTime
            .parse(((TextNode) show.selectFirst("div.info").childNode(0)).text(),
                Constants.DATE_TIME_FORMATTER);
        List<String> images = show.selectFirst("#pictureurls").select(CssSelectors.TAG_IMG)
            .eachAttr("rel");
        CelebrityAlbum album = new CelebrityAlbum(id, type, title, updateTime, images);
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
        String next = show.selectFirst("div.next").selectFirst(CssSelectors.TAG_A)
            .attr(CssSelectors.ATTR_HREF);
        if (!LAST_PAGE.equals(next)) {
            album.setNext(Integer
                .parseInt(RegexUtils.matchesOrElseThrow(ALBUM_HREF_REGEX, next).group("id")));
        }
        return album;
    }

    /**
     * Obtains details of the adult video of the given code
     *
     * @param id ignore case
     */
    public CelebrityAdultEntry findAdultEntry(String id) throws HttpResponseException {
        Document document = getDocument(
            builder0("/fanhao/%s.html", AssertUtils.requireNotBlank(id).toUpperCase()),
            SnapshotStrategy.never());
        Element div = document.selectFirst("div.fanhao");
        if (div.childNodeSize() == 1) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, div.text());
        }
        String cover = div.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);

        Elements ems = div.select("em");
        Map<String, String> info = ems.stream().collect(
            Collectors.toMap(e -> e.text().replace(" ", ""),
                em -> ((TextNode) em.nextSibling()).text().substring(1)));
        AdultEntry entry = Objects
            .requireNonNull(AdultEntryUtils.getAdultEntry(info, cover, SEPARATOR));
        Element current = document.selectFirst("a.current");
        if (current != null) {
            return new CelebrityAdultEntry(entry, initCelebrity(document, SimpleCelebrity::new));
        }
        return new CelebrityAdultEntry(entry);
    }

    /**
     * Initializes basic properties of a celebrity.
     */
    private <T extends SimpleCelebrity> T initCelebrity(Document document,
        TriFunction<? super Integer, String, CelebrityType, T> constructor) {
        Matcher matcher = RegexUtils.matchesOrElseThrow(CELEBRITY_HREF_REGEX,
            document.selectFirst("a.current").attr(CssSelectors.ATTR_HREF));
        int id = Integer.parseInt(matcher.group("id"));
        String name = document.selectFirst(".mx_name").text();
        CelebrityType type = EnumUtilExt
            .deserializeText(matcher.group("t"), CelebrityType.class, false);
        T t = constructor.apply(id, name, type);
        Element block01 = document.selectFirst("div.cm_block01");
        String image = block01.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);
        if (!NO_PERSON_IMG.equals(image)) {
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
        info.setGender(AdultEntryUtils.getValue(map, s -> {
            if (FAKE_FEMALE.equals(s)) {
                return Gender.MALE;
            }
            return EnumUtilExt.deserializeTitle(s, Gender.class, false);
        }, "性别"));
        info.setBirthday(AdultEntryUtils.getValue(map, this::parseBirthday, "出生日期"));
        info.setFullName(AdultEntryUtils.getString(map, "姓名"));
        info.setZhNames(AdultEntryUtils.getValues(map, Function.identity(), SEPARATOR, "中文名"));
        info.setJaNames(AdultEntryUtils.getStringList(map, SEPARATOR, "日文名"));
        info.setEnNames(AdultEntryUtils.getStringList(map, SEPARATOR, "英文名"));
        info.setAka(AdultEntryUtils.getStringList(map, SEPARATOR, "别名"));
        info.setZodiac(AdultEntryUtils.getValue(map,
            text -> EnumUtilExt.deserializeTitle(text.substring(0, 1), Zodiac.class, false), "生肖"));
        info.setConstellation(
            AdultEntryUtils.getValue(map, text -> deserializeAka(text, Constellation.class), "星座"));
        info.setInterests(AdultEntryUtils.getStringList(map, SEPARATOR, "兴趣", "爱好"));

        info.setHeight(AdultEntryUtils.getValueIfMatched(map, HEIGHT_REGEX, matcher -> {
            double height = Double.parseDouble(matcher.group("h"));
            if (height < CENTIMETERS_PER_METER) {
                height *= CENTIMETERS_PER_METER;
            }
            return (int) Math.round(height);
        }, "身高"));
        info.setWeight(AdultEntryUtils.getValueIfMatched(map, WEIGHT_REGEX,
            matcher -> (int) Math.round(Double.parseDouble(matcher.group("w"))), "体重"));
        info.setCup(AdultEntryUtils.getValue(map,
            text -> Enum.valueOf(CupEnum.class,
                RegexUtils.matchesOrElseThrow(CUP_REGEX, text.strip()).group("c")),
            "罩杯"));
        info.setFigure(AdultEntryUtils.getValue(map, this::parseFigure, "三围"));
        info.setBloodType(AdultEntryUtils.getValueIfMatched(map, BLOOD_REGEX, matcher -> {
            String type = matcher.group("t");
            return "0".equals(type) ? BloodType.O
                : Enum.valueOf(BloodType.class, type.toUpperCase());
        }, "血型"));

        info.setOccupations(AdultEntryUtils.getStringList(map, "、，/., ", "职业"));
        info.setStart(
            AdultEntryUtils.getValue(map, text -> parsePartDate(text.replace(" ", "")), "出道时间"));
        info.setRetire(AdultEntryUtils.getValue(map, this::parsePartDate, "隐退时间"));
        info.setAgency(AdultEntryUtils.getString(map, "经纪公司"));
        info.setFirm(AdultEntryUtils.getString(map, "事务所"));
        info.setSchool(AdultEntryUtils.getString(map, "毕业院校"));
        info.setBirthplace(AdultEntryUtils.getString(map, "出生地"));
        info.setNation(
            AdultEntryUtils.getValue(map, text -> deserializeAka(text, Nation.class), "民族"));
        info.setNationalities(AdultEntryUtils.getValue(map,
            text -> deserializeAkaList(text.replace(" ", ""), Region.class, "、,"), "国籍"));
        info.setLanguages(AdultEntryUtils.getValue(map,
            text -> deserializeAkaList(StringUtils.stripEnd(text, "等"), Language.class, "、/ ，及"),
            "语言"));

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

    private <T extends Enum<T> & AkaPredicate<String>> List<T> deserializeAkaList(String text,
        Class<T> clazz,
        String separatorChars) {
        Set<T> ts = new HashSet<>();
        for (String s : StringUtils.split(text, separatorChars)) {
            CollectionUtils.addIgnoreNull(ts, deserializeAka(s, clazz));
        }
        return ts.isEmpty() ? null : new ArrayList<>(ts);
    }

    private Temporal parsePartDate(String text) {
        Matcher ymd = LOCAL_DATE_REGEX.matcher(text);
        if (ymd.find()) {
            try {
                return LocalDate
                    .of(Integer.parseInt(ymd.group("y")), Integer.parseInt(ymd.group("ymd")),
                        Integer.parseInt(ymd.group("d")));
            } catch (DateTimeException e) {
                return null;
            }
        }
        Matcher ym = YEAR_MONTH_REGEX.matcher(text);
        if (ym.matches()) {
            return YearMonth.of(Integer.parseInt(ym.group("y")), Integer.parseInt(ym.group("ymd")));
        }
        Matcher y = YEAR_REGEX.matcher(text);
        if (y.matches()) {
            return Year.of(Integer.parseInt(y.group("y")));
        }
        return null;
    }

    private Figure parseFigure(String text) {
        text = text.replace("cm", "");
        Matcher m = FIGURE_REGEX.matcher(text);
        if (m.matches()) {
            Figure figure = new Figure(Integer.parseInt(m.group("b")),
                Integer.parseInt(m.group("w")),
                Integer.parseInt(m.group("h")));
            String cup = m.group("c");
            if (cup != null) {
                figure.setCup(Enum.valueOf(CupEnum.class, cup));
            }
            return figure;
        }
        text = text.replace(" ", "");
        Matcher m2 = FIGURE_REGEX2.matcher(text);
        if (m2.matches()) {
            Figure figure = new Figure(Integer.parseInt(m2.group("b")),
                Integer.parseInt(m2.group("w")),
                Integer.parseInt(m2.group("h")));
            String cup = m2.group("c");
            if (cup != null) {
                figure.setCup(Enum.valueOf(CupEnum.class, cup));
            }
            return figure;
        }
        return null;
    }

    private List<String> getExtStringList(Map<String, Element> extInfo, String key) {
        Element nextEle = extInfo.remove(key);
        if (nextEle == null) {
            return null;
        }
        Elements ps = nextEle.select("p");
        if (ps.isEmpty()) {
            return List.of(nextEle.text());
        }
        return ps.eachText();
    }

    private List<SimpleAdultEntry> getWorks(Element icon) {
        List<SimpleAdultEntry> works = new ArrayList<>();
        Element tbody = icon.selectFirst("tbody");
        if (tbody != null) {
            Elements trs = tbody.select(CssSelectors.TAG_TR);
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                if (tds.size() < 6) {
                    continue;
                }
                String[] values =
                    tds.stream().map(Element::text).map(s -> StringUtils.isBlank(s) ? null : s)
                        .toArray(String[]::new);
                if (values[0] == null) {
                    continue;
                }
                SimpleAdultEntry work = new SimpleAdultEntry(values[0]);
                if (!BLOCKED_TITLE.equals(values[1])) {
                    work.setTitle(values[1]);
                }
                work.setMosaic(getValue(text -> {
                    text = text.replace(" ", "");
                    if (Mosaic.COVERED.getTitle().equals(text)) {
                        return Mosaic.COVERED;
                    }
                    if (Mosaic.UNCOVERED.getTitle().equals(text)) {
                        return Mosaic.UNCOVERED;
                    }
                    return null;
                }, values, 2));
                work.setDuration(getValue(text -> {
                    Matcher matcher = DURATION_REGEX.matcher(text);
                    return matcher.matches() ? Duration
                        .ofMinutes(Long.parseLong(matcher.group("d"))) : null;
                }, values, 3, 2, 5));
                work.setRelease(getValue(text -> {
                    Matcher m = RELEASE_REGEX.matcher(text);
                    if (!m.matches()) {
                        return null;
                    }
                    LocalDate date = LocalDate
                        .of(Integer.parseInt(m.group("y")), Integer.parseInt(m.group("m")),
                            Integer.parseInt(m.group("d")));
                    return LocalDate.EPOCH.equals(date) ? null : date;
                }, values, 4, 3));
                work.setDistributor(getValue(s -> s, values, 5, 3));
                works.add(work);
            }
        }
        Elements labels = icon.select("label.born");
        if (!labels.isEmpty()) {
            labels.eachText().stream().map(SimpleAdultEntry::new).forEach(works::add);
        }
        return works.isEmpty() ? null : works;
    }

    private <T> T getValue(Function<? super String, T> function, String[] values, int... indexes) {
        for (int i : indexes) {
            String value = values[i];
            if (value != null) {
                T t = function.apply(value);
                if (t != null) {
                    values[i] = null;
                    return t;
                }
            }
        }
        return null;
    }

    private Set<SimpleAlbum> getAlbums(Document document) {
        Elements lis = document.selectFirst("#xiezhen").select(CssSelectors.TAG_LI);
        if (lis.isEmpty()) {
            return null;
        }
        Set<SimpleAlbum> albums = new HashSet<>();
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_A);
            Matcher matcher = RegexUtils
                .matchesOrElseThrow(ALBUM_HREF_REGEX, a.attr(CssSelectors.ATTR_HREF));
            int albumId = Integer.parseInt(matcher.group("id"));
            AlbumType type = EnumUtilExt
                .deserializeText(matcher.group("t"), AlbumType.class, false);
            String title = a.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_ALT);
            albums.add(new SimpleAlbum(albumId, type, title));
        }
        return albums;
    }
}
