package wsg.tools.internet.video.site.adult.mr;

import lombok.Getter;
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
import wsg.tools.internet.base.BasicHttpSession;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.intf.*;
import wsg.tools.internet.common.CssSelector;
import wsg.tools.internet.common.Scheme;
import wsg.tools.internet.video.enums.Constellation;
import wsg.tools.internet.video.enums.Gender;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RegionEnum;
import wsg.tools.internet.video.site.adult.mr.enums.*;

import java.io.IOException;
import java.time.*;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kingen
 * @see <a href="http://www.mrenbaike.net/">Wiki of Celebrities</a>
 * @since 2021/2/24
 */
public class CelebrityWikiSite extends BaseSite {

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
    private static final Pattern LOCAL_DATE_REGEX =
            Pattern.compile("(?<y>(18|19|20)\\d{2})([年./-])(?<m>0?[1-9]|1[012])(月|\\3)(?<d>0?[1-9]|[12][0-9]|3[01])(?!\\d)");
    private static final Pattern YEAR_MONTH_REGEX = Pattern.compile("(?<y>(18|19|20)\\d{2})[年.-](?<m>0?[1-9]|1[012])(月[^\\d]*)?");
    private static final Pattern YEAR_REGEX = Pattern.compile("(?<y>(18|19|20)\\d{2})(年[^\\d]*)?");
    private static final Pattern MONTH_DAY_REGEX = Pattern.compile("(?<m>0?[1-9]|1[012])[月./-](?<d>0?[1-9]|[12][0-9]|3[01])([日号].*)?");
    private static final Pattern HEIGHT_REGEX = Pattern.compile("(?<h>[12]\\d{2}(\\.\\d)?|[12]\\.\\d{2}) ?(cm|CM|厘米|公分|m|㎝)?([(（][^()]+[）)])?");
    private static final Pattern WEIGHT_REGEX = Pattern.compile("(?<w>\\d{2,3}(\\.\\d)?) ?(kg|公斤|g|klg)([(（][^()]+[）)])?", Pattern.CASE_INSENSITIVE);
    private static final Pattern FIGURE_REGEX =
            Pattern.compile("(?<b>\\d{2,3})(?<c>[A-L])?([^\\d]+)(?<w>\\d{2})\\3(?<h>\\d{2,3})[^\\d]{0,4}", Pattern.CASE_INSENSITIVE);
    private static final Pattern FIGURE_REGEX2 =
            Pattern.compile("B?([.:]?)(?<b>\\d{2,3})([(（]?(?<c>[A-L])[）)]?)?([^\\d]*)W\\1(?<w>\\d{2})\\5H\\1(?<h>\\d{2,3})[^\\d]{0,3}", Pattern.CASE_INSENSITIVE);
    private static final Pattern CUP_REGEX = Pattern.compile("(?<c>[A-P])(罩?杯?|-\\d{2,3})");
    private static final Pattern BLOOD_REGEX = Pattern.compile("(?<t>[ABO0]|AB)( ?型)?", Pattern.CASE_INSENSITIVE);
    private static final String BLOCKED_TITLE = "****名人百科网根据服务器要求进行屏蔽****";
    private static final Pattern DURATION_REGEX = Pattern.compile("(?<d>\\d{2,4}) ?(分钟|分|钟)");
    private static final Pattern RELEASE_REGEX = Pattern.compile("(?<y>\\d{4})([-/])(?<m>\\d{1,2})\\2(?<d>\\d{1,2})");

    private static CelebrityWikiSite instance;

    static {
        String celebrityTypes = Arrays.stream(CelebrityType.values()).map(TextSupplier::getText).collect(Collectors.joining("|"));
        CELEBRITY_HREF_REGEX = Pattern.compile("http://www\\.mrenbaike\\.net/(?<t>" + celebrityTypes + ")/m(?<id>\\d+)/((info|pic|news|comm)\\.html)?");
        String albumTypes = Arrays.stream(AlbumType.values()).map(TextSupplier::getText).collect(Collectors.joining("|"));
        ALBUM_HREF_REGEX = Pattern.compile("http://www\\.mrenbaike\\.net/tuku/(?<t>" + albumTypes + ")/(?<id>\\d+)(_(?<i>\\d+))?\\.html");
    }

    @Getter
    private final IntRangeRepository<Celebrity> celebrityRepository = new IntRangeRepositoryImpl<>(this::findCelebrity, () -> 9601);
    @Getter
    private final Repository<String, CelebrityAdultVideo> videoRepository = this::findAdultVideo;
    @Getter
    private final Map<AlbumType, IterableRepository<Album>> albumRepositories;

    private CelebrityWikiSite() {
        super("Wiki of Celebrities", new BasicHttpSession(Scheme.HTTP, "mrenbaike.net"), new AbstractResponseHandler<>() {
            @Override
            public String handleEntity(HttpEntity entity) throws IOException {
                String content = EntityUtils.toString(entity);
                Document document = Jsoup.parse(content);
                if (TIP_MSG.equals(document.title())) {
                    throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, document.selectFirst("div.content").text());
                }
                return content;
            }
        });
        albumRepositories = new HashMap<>(AlbumType.values().length);
        for (AlbumType type : AlbumType.values()) {
            albumRepositories.put(type, new IterableRepositoryImpl<>(id -> this.findAlbum(id, type), type.getFirst()));
        }
    }

    public synchronized static CelebrityWikiSite getInstance() {
        if (instance == null) {
            instance = new CelebrityWikiSite();
        }
        return instance;
    }

    public Album findAlbum(int id, AlbumType type) throws HttpResponseException {
        Document document = getDocument(builder0("/tuku/%s/%d.html", type.getText(), id), SnapshotStrategy.never());
        Element show = document.selectFirst("div.picshow");
        String title = show.selectFirst(CssSelector.TAG_H1).text();
        LocalDateTime updateTime = LocalDateTime.parse(((TextNode) show.selectFirst("div.info").childNode(0)).text(), Constants.STANDARD_DATE_TIME_FORMATTER);
        List<String> images = show.selectFirst("#pictureurls").select("img").eachAttr("rel");
        Album album = new Album(id, type, title, updateTime, images);
        Element text = show.selectFirst("div.text");
        List<String> hrefs = text.select(">a").eachAttr(CssSelector.ATTR_HREF).stream().filter(s -> !HOME_PAGE.equals(s)).collect(Collectors.toList());
        if (!hrefs.isEmpty()) {
            album.setRelatedCelebrities(hrefs.stream().map(s -> RegexUtils.matchesOrElseThrow(CELEBRITY_HREF_REGEX, s).group("id")).map(Integer::parseInt)
                    .collect(Collectors.toList()));
        }
        Element tags = text.selectFirst("div.ptags");
        if (tags != null) {
            album.setTags(tags.select(CssSelector.TAG_A).eachText());
        }
        String next = show.selectFirst("div.next").selectFirst(CssSelector.TAG_A).attr(CssSelector.ATTR_HREF);
        if (!LAST_PAGE.equals(next)) {
            album.setNext(Integer.parseInt(RegexUtils.matchesOrElseThrow(ALBUM_HREF_REGEX, next).group("id")));
        }
        return album;
    }

    /**
     * Obtains details of the adult video of the given code
     *
     * @param code ignore case
     */
    public CelebrityAdultVideo findAdultVideo(String code) throws HttpResponseException {
        code = AssertUtils.requireNotBlank(code).toUpperCase();
        Document document = getDocument(builder0("/fanhao/%s.html", code), SnapshotStrategy.never());
        Element div = document.selectFirst("div.fanhao");
        if (div.childNodeSize() == 1) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, div.text());
        }
        String cover = div.selectFirst("img").attr(CssSelector.ATTR_SRC);
        CelebrityAdultVideo video = new CelebrityAdultVideo(code, cover);
        Element current = document.selectFirst("a.current");
        if (current != null) {
            video.setCelebrity(initCelebrity(document, SimpleCelebrity::new));
        }

        Elements ems = div.select("em");
        Map<String, String> info = ems.stream()
                .collect(Collectors.toMap(e -> e.text().replace(" ", ""), em -> ((TextNode) em.nextSibling()).text().substring(1)));
        video.setActress(getString(info, "演员"));
        video.setTitle(getString(info, "名称"));
        video.setRelease(getValue(info, "发行时间", LocalDate::parse));
        video.setDuration(getValue(info, "播放时间", s -> Duration.ofMinutes(Long.parseLong(RegexUtils.matchesOrElseThrow(DURATION_REGEX, s).group("d")))));
        video.setDirector(getValue(info, "导演", s -> "TODO".equals(s) ? null : s));
        video.setMosaic(getValue(info, "是否有码", s -> EnumUtilExt.deserializeTitle(s, Mosaic.class, false)));
        video.setProducer(getString(info, "制作商"));
        video.setDistributor(getString(info, "发行商"));
        video.setSeries(getString(info, "系列"));
        video.setTags(getStringList(info, "类别"));
        return video;
    }

    public Celebrity findCelebrity(int id) throws HttpResponseException {
        Document document = getDocument(builder0("/yule/m%d/info.html", id), SnapshotStrategy.never());
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
        Celebrity celebrity = initCelebrity(document, (i, name, type) -> new Celebrity(i, name, type, info));
        Element ext = document.selectFirst("div.dataext");
        if (ext != null) {
            Map<String, Element> extInfo = ext.select("h2.itit").stream().filter(Element::hasText)
                    .collect(Collectors.toMap(e -> e.hasAttr("id") ? e.id() : e.text(), Element::nextElementSibling));
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
        celebrity.setAlbums(getAlbums(getDocument(builder0("/yule/m%s/pic.html", id), SnapshotStrategy.never())));
        return celebrity;
    }

    /**
     * Initializes basic properties of a celebrity.
     */
    private <T extends SimpleCelebrity> T initCelebrity(Document document, TriFunction<Integer, String, CelebrityType, T> constructor) {
        Matcher matcher = RegexUtils.matchesOrElseThrow(CELEBRITY_HREF_REGEX, document.selectFirst("a.current").attr(CssSelector.ATTR_HREF));
        int id = Integer.parseInt(matcher.group("id"));
        String name = document.selectFirst(".mx_name").text();
        CelebrityType type = EnumUtilExt.deserializeText(matcher.group("t"), CelebrityType.class, false);
        T t = constructor.apply(id, name, type);
        Element block01 = document.selectFirst("div.cm_block01");
        String image = block01.selectFirst("img").attr(CssSelector.ATTR_SRC);
        if (!NO_PERSON_IMG.equals(image)) {
            t.setCover(image);
        }
        Elements tags = block01.selectFirst("div.txt03").select(CssSelector.TAG_A);
        if (!tags.isEmpty()) {
            t.setTags(tags.stream().map(Element::text).collect(Collectors.toList()));
        }
        return t;
    }

    private BasicInfo getBasicInfo(Map<String, String> map) {
        BasicInfo info = new BasicInfo();
        info.setGender(getValue(map, "性别", s -> FAKE_FEMALE.equals(s) ? Gender.MALE : EnumUtilExt.deserializeTitle(s, Gender.class, false)));
        info.setBirthday(getValue(map, "出生日期", this::parseBirthday));
        info.setFullName(getString(map, "姓名"));
        info.setZhNames(getStringList(map, "中文名"));
        info.setJaNames(getStringList(map, "日文名"));
        info.setEnNames(getStringList(map, "英文名"));
        info.setAka(getStringList(map, "别名"));
        info.setZodiac(getValue(map, "生肖", text -> EnumUtilExt.deserializeTitle(text.substring(0, 1), Zodiac.class, false)));
        info.setConstellation(getValue(map, "星座", text -> deserializeAka(text, Constellation.class)));
        List<String> interests = getStringList(map, "兴趣");
        info.setInterests(interests != null ? interests : getStringList(map, "爱好"));

        info.setHeight(getValueMatched(map, "身高", HEIGHT_REGEX, matcher -> {
            double height = Double.parseDouble(matcher.group("h"));
            if (height < CENTIMETERS_PER_METER) {
                height *= CENTIMETERS_PER_METER;
            }
            return (int) Math.round(height);
        }));
        info.setWeight(getValueMatched(map, "体重", WEIGHT_REGEX, matcher -> (int) Math.round(Double.parseDouble(matcher.group("w")))));
        info.setCup(getValue(map, "罩杯", text -> Enum.valueOf(CupEnum.class, RegexUtils.matchesOrElseThrow(CUP_REGEX, text.strip()).group("c"))));
        info.setFigure(getValue(map, "三围", this::parseFigure));
        info.setBloodType(getValueMatched(map, "血型", BLOOD_REGEX, matcher -> {
            String type = matcher.group("t");
            return "0".equals(type) ? BloodType.O : Enum.valueOf(BloodType.class, type.toUpperCase());
        }));

        info.setOccupations(getValue(map, "职业", text -> deserializeAkaList(text, Occupation.class, "、，/., ")));
        info.setStart(getValue(map, "出道时间", text -> parsePartDate(text.replace(" ", ""))));
        info.setRetire(getValue(map, "隐退时间", this::parsePartDate));
        info.setAgency(getString(map, "经纪公司"));
        info.setFirm(getString(map, "事务所"));
        info.setSchool(getString(map, "毕业院校"));
        info.setBirthplace(getString(map, "出生地"));
        info.setNation(getValue(map, "民族", text -> text.contains(Nation.MIXED_KEY) ? Nation.MIXED : deserializeAka(text, Nation.class)));
        info.setNationalities(getValue(map, "国籍", text -> deserializeAkaList(text.replace(" ", ""), RegionEnum.class, "、,")));
        info.setLanguages(getValue(map, "语言", text -> deserializeAkaList(StringUtils.stripEnd(text, "等"), LanguageEnum.class, "、/ ，及")));

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
            return MonthDay.of(Integer.parseInt(matcher.group("m")), Integer.parseInt(matcher.group("d")));
        }
        return null;
    }

    private <T extends Enum<T> & AkaPredicate<String>> T deserializeAka(String text, Class<T> clazz) {
        try {
            return EnumUtilExt.deserializeAka(text, clazz);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private <T extends Enum<T> & AkaPredicate<String>> List<T> deserializeAkaList(String text, Class<T> clazz, String separatorChars) {
        Set<T> ts = new HashSet<>();
        for (String s : StringUtils.split(text, separatorChars)) {
            CollectionUtils.addIgnoreNull(ts, deserializeAka(s, clazz));
        }
        return ts.isEmpty() ? null : new ArrayList<>(ts);
    }

    private String getString(Map<String, String> map, String key) {
        return getValue(map, key, Function.identity());
    }

    private <T> T getValueMatched(Map<String, String> map, String key, Pattern pattern, Function<Matcher, T> function) {
        return getValue(map, key, text -> {
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                return function.apply(matcher);
            }
            return null;
        });
    }

    private List<String> getStringList(Map<String, String> map, String key) {
        return getValue(map, key, text -> Arrays.asList(StringUtils.split(text, SEPARATOR)));
    }

    private <T> T getValue(Map<String, String> map, String key, Function<String, T> function) {
        String value = map.remove(key);
        if (value == null) {
            return null;
        }
        return function.apply(value);
    }

    private Temporal parsePartDate(String text) {
        Matcher m = LOCAL_DATE_REGEX.matcher(text);
        if (m.find()) {
            try {
                return LocalDate.of(Integer.parseInt(m.group("y")), Integer.parseInt(m.group("m")), Integer.parseInt(m.group("d")));
            } catch (DateTimeException e) {
                return null;
            }
        }
        m = YEAR_MONTH_REGEX.matcher(text);
        if (m.matches()) {
            return YearMonth.of(Integer.parseInt(m.group("y")), Integer.parseInt(m.group("m")));
        }
        m = YEAR_REGEX.matcher(text);
        if (m.matches()) {
            return Year.of(Integer.parseInt(m.group("y")));
        }
        return null;
    }

    private Figure parseFigure(String text) {
        text = text.replace("cm", "");
        Matcher m = FIGURE_REGEX.matcher(text);
        if (m.matches()) {
            Figure figure = new Figure(Integer.parseInt(m.group("b")), Integer.parseInt(m.group("w")), Integer.parseInt(m.group("h")));
            String cup = m.group("c");
            if (cup != null) {
                figure.setCup(Enum.valueOf(CupEnum.class, cup));
            }
            return figure;
        }
        text = text.replace(" ", "");
        m = FIGURE_REGEX2.matcher(text);
        if (m.matches()) {
            Figure figure = new Figure(Integer.parseInt(m.group("b")), Integer.parseInt(m.group("w")), Integer.parseInt(m.group("h")));
            String cup = m.group("c");
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

    private List<SimpleAdultVideo> getWorks(Element icon) {
        List<SimpleAdultVideo> works = new ArrayList<>();
        Element tbody = icon.selectFirst("tbody");
        if (tbody != null) {
            Elements trs = tbody.select(CssSelector.TAG_TR);
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                if (tds.size() < 6) {
                    continue;
                }
                String[] values = tds.stream().map(Element::text).map(s -> StringUtils.isBlank(s) ? null : s).toArray(String[]::new);
                if (values[0] == null) {
                    continue;
                }
                SimpleAdultVideo work = new SimpleAdultVideo(values[0]);
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
                    return matcher.matches() ? Duration.ofMinutes(Long.parseLong(matcher.group("d"))) : null;
                }, values, 3, 2, 5));
                work.setRelease(getValue(text -> {
                    Matcher m = RELEASE_REGEX.matcher(text);
                    if (!m.matches()) {
                        return null;
                    }
                    LocalDate date = LocalDate.of(Integer.parseInt(m.group("y")), Integer.parseInt(m.group("m")), Integer.parseInt(m.group("d")));
                    return LocalDate.EPOCH.equals(date) ? null : date;
                }, values, 4, 3));
                work.setDistributor(getValue(s -> s, values, 5, 3));
                works.add(work);
            }
        }
        Elements labels = icon.select("label.born");
        if (!labels.isEmpty()) {
            labels.eachText().stream().map(SimpleAdultVideo::new).forEach(works::add);
        }
        return works.isEmpty() ? null : works;
    }

    private <T> T getValue(Function<String, T> function, String[] values, int... indexes) {
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
        Elements lis = document.selectFirst("#xiezhen").select(CssSelector.TAG_LI);
        if (lis.isEmpty()) {
            return null;
        }
        Set<SimpleAlbum> albums = new HashSet<>();
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelector.TAG_A);
            Matcher matcher = RegexUtils.matchesOrElseThrow(ALBUM_HREF_REGEX, a.attr(CssSelector.ATTR_HREF));
            int albumId = Integer.parseInt(matcher.group("id"));
            AlbumType type = EnumUtilExt.deserializeText(matcher.group("t"), AlbumType.class, false);
            String title = a.selectFirst("img").attr("alt");
            albums.add(new SimpleAlbum(albumId, type, title));
        }
        return albums;
    }
}
