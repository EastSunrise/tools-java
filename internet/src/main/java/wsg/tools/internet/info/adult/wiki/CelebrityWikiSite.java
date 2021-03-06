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
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.RequestBuilder;
import org.jetbrains.annotations.Contract;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.function.TriFunction;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.page.AmountCountablePage;
import wsg.tools.internet.base.page.Page;
import wsg.tools.internet.base.page.PageIndex;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.enums.BloodType;
import wsg.tools.internet.common.enums.Zodiac;
import wsg.tools.internet.info.adult.common.AdultEntryParser;
import wsg.tools.internet.info.adult.common.CupEnum;

/**
 * @author Kingen
 * @see <a href="http://www.mrenbaike.net/">Wiki of Celebrities</a>
 * @since 2021/2/24
 */
@ConcreteSite
public final class CelebrityWikiSite extends BaseSite
    implements RepoRetrievable<WikiCelebrityIndex, WikiCelebrity> {

    private static final String TIP_MSG = "提示信息";
    private static final String VALUE_NULL = "暂无";
    private static final String SEPARATOR = "、";
    private static final String HOME_PAGE = "http://www.mrenbaike.net";
    private static final String NO_PERSON_IMG = "noperson.jpg";

    public CelebrityWikiSite() {
        super("Wiki of Celebrities", httpHost("mrenbaike.net"));
    }

    /**
     * Returns the repository of all celebrities.
     */
    @Nonnull
    @Contract(" -> new")
    public ListRepository<WikiCelebrityIndex, WikiCelebrity> getRepository()
        throws OtherResponseException {
        return Repositories.list(this, findAllCelebrityIndices());
    }

    /**
     * Retrieves all indices of celebrities.
     */
    @Nonnull
    public List<WikiCelebrityIndex> findAllCelebrityIndices() throws OtherResponseException {
        Document document = findDocument(httpGet("/comp/sitemap"));
        return document.select(".ulist").last().select(CssSelectors.TAG_A)
            .stream().map(this::getIndex).collect(Collectors.toList());
    }

    /**
     * Retrieves the paged result of the indices under the given subtype.
     */
    @Nonnull
    public AmountCountablePage<WikiCelebrityIndex>
    findAll(@Nonnull PageIndex pageIndex, @Nullable WikiCelebrityType type)
        throws NotFoundException, OtherResponseException {
        int current = pageIndex.getCurrent();
        String page = current == 0 ? "" : ("_" + (current + 1));
        String text = Optional.ofNullable(type).map(WikiCelebrityType::getAsPath)
            .orElse("mingren");
        Document document = getDocument(httpGet("/%s/index%s.html", text, page));

        Element box = document.selectFirst(".personbox");
        Elements as = box.selectFirst(CssSelectors.TAG_UL).select(CssSelectors.TAG_A);
        List<WikiCelebrityIndex> indices = as.stream()
            .map(this::getIndex).collect(Collectors.toList());

        int total;
        Element a1 = box.selectFirst(".pagePg").selectFirst(".a1");
        if (a1 == null) {
            total = indices.size();
        } else {
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.TOTAL_REGEX, a1.text());
            total = Integer.parseInt(matcher.group("t"));
        }
        return Page.amountCountable(indices, pageIndex, 100, total);
    }

    /**
     * Retrieves the celebrity of the specified identifier.
     *
     * @see #findAllCelebrityIndices()
     * @see #findAll(PageIndex, WikiCelebrityType)
     */
    @Nonnull
    @Override
    public WikiCelebrity findById(@Nonnull WikiCelebrityIndex index)
        throws NotFoundException, OtherResponseException {
        int id = index.getId();
        WikiCelebrityType subtype = index.getSubtype();
        Document document = getDocument(httpGet("/%s/m%d/info.html", subtype.getAsPath(), id));
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
        celebrity.setAlbums(getSimpleAlbums(subtype, id));
        return celebrity;
    }

    /**
     * Retrieves an album of the given identifier.
     */
    @Nonnull
    public WikiAlbum findAlbum(@Nonnull WikiAlbumIndex index)
        throws NotFoundException, OtherResponseException {
        int id = index.getId();
        WikiAlbumType type = index.getSubtype();
        Document document = getDocument(httpGet("/tuku/%s/%d.html", type.getAsPath(), id));
        Element show = document.selectFirst("div.picshow");
        String title = show.selectFirst(CssSelectors.TAG_H1).text();
        String timeStr = ((TextNode) show.selectFirst(".info").childNode(0)).text();
        LocalDateTime updateTime = LocalDateTime.parse(timeStr, Constants.YYYY_MM_DD_HH_MM_SS);
        List<URL> images = show.selectFirst("#pictureurls").select(CssSelectors.TAG_IMG)
            .stream().map(img -> img.attr(CssSelectors.ATTR_REL)).map(NetUtils::createURL)
            .collect(Collectors.toList());
        WikiAlbum album = new WikiAlbum(id, type, title, updateTime, images);
        Element text = show.selectFirst(".text");
        List<WikiCelebrityIndex> related = new ArrayList<>();
        for (Element a : text.select(">a")) {
            String href = a.attr(CssSelectors.ATTR_HREF);
            if (href.isEmpty() || HOME_PAGE.equals(href)) {
                continue;
            }
            related.add(getIndex(a));
        }
        if (!related.isEmpty()) {
            album.setRelatedCelebrities(related);
        }
        Element tags = text.selectFirst("div.ptags");
        if (tags != null) {
            album.setTags(new HashSet<>(tags.select(CssSelectors.TAG_A).eachText()));
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
     *
     * @see WikiCelebrity#getWorks()
     */
    @Nonnull
    public WikiAdultEntry findAdultEntry(@Nonnull String id)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = null;
        try {
            builder = httpGet("/fanhao/%s.html", id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        }
        Document document = getDocument(builder);
        WikiSimpleCelebrity celebrity = initCelebrity(document, WikiSimpleCelebrity::new);
        Element div = document.selectFirst("div.fanhao");
        if (div.childNodeSize() == 1) {
            throw new NotFoundException(div.text());
        }

        Elements ems = div.select(CssSelectors.TAG_EM);
        Map<String, String> info = new HashMap<>(ems.size());
        for (Element em : ems) {
            String key = em.text().replace(" ", "");
            String value = ((TextNode) em.nextSibling()).text().substring(1);
            MapUtilsExt.putIfAbsent(info, key, value);
        }
        String src = div.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);
        URL cover = NetUtils.createURL(src);
        AdultEntryParser parser = AdultEntryParser.create(info);
        String serialNum = parser.getSerialNum();
        String title = parser.getTitle();
        List<String> actresses = parser.getActresses(SEPARATOR);
        WikiAdultEntry entry = new WikiAdultEntry(celebrity, serialNum, title, cover, actresses);
        entry.setMosaic(parser.getMosaic());
        entry.setDuration(parser.getDuration());
        entry.setPublish(parser.getPublish());
        entry.setDirector(parser.getDirector());
        entry.setProducer(parser.getProducer());
        entry.setDistributor(parser.getDistributor());
        entry.setSeries(parser.getSeries());
        entry.setTags(parser.getTags(SEPARATOR));
        parser.check("整理");
        return entry;
    }

    /**
     * Initializes basic properties of a celebrity.
     */
    private <T extends WikiSimpleCelebrity> T initCelebrity(Document document,
        TriFunction<Integer, String, WikiCelebrityType, T> constructor) {
        Element current = document.selectFirst(".current");
        if (current == null) {
            return null;
        }
        WikiCelebrityIndex index = getIndex(current);
        String name = document.selectFirst(".mx_name").text();
        T t = constructor.apply(index.getId(), name, index.getSubtype());
        Element block01 = document.selectFirst("div.cm_block01");
        String image = block01.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_SRC);
        if (!image.endsWith(NO_PERSON_IMG)) {
            t.setCover(NetUtils.createURL(image));
        }
        Elements tags = block01.selectFirst("div.txt03").select(CssSelectors.TAG_A);
        if (!tags.isEmpty()) {
            t.setTags(new HashSet<>(tags.eachText()));
        }
        return t;
    }

    private WikiCelebrityIndex getIndex(Element a) {
        String href = a.attr(CssSelectors.ATTR_HREF);
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.CELEBRITY_URL_REGEX, href);
        String t = matcher.group("t");
        WikiCelebrityType type = EnumUtilExt
            .valueOfKey(WikiCelebrityType.class, t, WikiCelebrityType::getAsPath);
        int id = Integer.parseInt(matcher.group("id"));
        return new WikiSimpleCelebrity(id, a.text(), type);
    }

    @Nonnull
    private BasicInfo getBasicInfo(Map<String, String> map) {
        BasicInfo info = new BasicInfo();
        info.setGender(MapUtilsExt.getString(map, "性别"));
        info.setFullName(MapUtilsExt.getString(map, "姓名"));
        info.setZhNames(MapUtilsExt.getStringList(map, SEPARATOR, "中文名"));
        info.setJaNames(MapUtilsExt.getStringList(map, SEPARATOR, "日文名"));
        info.setEnNames(MapUtilsExt.getStringList(map, SEPARATOR, "英文名"));
        info.setAka(MapUtilsExt.getStringList(map, SEPARATOR, "别名"));

        info.setZodiac(MapUtilsExt.getValue(map,
            text -> EnumUtilExt.valueOfAlias(Zodiac.class, text.substring(0, 1)), "生肖"));
        info.setConstellation(MapUtilsExt.getString(map, "星座"));
        info.setInterests(MapUtilsExt.getStringList(map, SEPARATOR, "兴趣", "爱好"));

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
        info.setNation(MapUtilsExt.getString(map, "民族"));

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

    private Set<String> getWorks(Element icon) {
        Set<String> works = new HashSet<>();
        Element tbody = icon.selectFirst("tbody");
        if (tbody != null) {
            Elements trs = tbody.select(CssSelectors.TAG_TR);
            for (Element tr : trs) {
                String serialNum = tr.selectFirst("td").text().strip();
                if (serialNum.isBlank()) {
                    continue;
                }
                works.add(serialNum.toUpperCase(Locale.ROOT));
            }
        }
        Elements labels = icon.select("label.born");
        if (!labels.isEmpty()) {
            for (String text : labels.eachText()) {
                works.add(text.toUpperCase(Locale.ROOT));
            }
        }
        return works;
    }

    @Nonnull
    private Set<WikiAlbumIndex> getSimpleAlbums(WikiCelebrityType type, int id)
        throws NotFoundException, OtherResponseException {
        Document document = getDocument(httpGet("/%s/m%s/pic.html", type.getAsPath(), id));
        Elements lis = document.selectFirst("#xiezhen").select(CssSelectors.TAG_LI);
        Set<WikiAlbumIndex> albums = new HashSet<>(lis.size());
        for (Element li : lis) {
            Element a = li.selectFirst(CssSelectors.TAG_A);
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.ALBUM_URL_REGEX, href);
            int albumId = Integer.parseInt(matcher.group("id"));
            String typeStr = matcher.group("t");
            WikiAlbumType albumType = EnumUtilExt
                .valueOfKey(WikiAlbumType.class, typeStr, WikiAlbumType::getAsPath);
            String title = a.selectFirst(CssSelectors.TAG_IMG).attr(CssSelectors.ATTR_ALT);
            albums.add(new WikiAlbum(albumId, albumType, title));
        }
        return albums;
    }

    @Nonnull
    @Override
    public Document getDocument(@Nonnull RequestBuilder builder)
        throws NotFoundException, OtherResponseException {
        Document document = super.getDocument(builder);
        if (TIP_MSG.equals(document.title())) {
            throw new NotFoundException(document.selectFirst("div.content").text());
        }
        return document;
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
            String types = Arrays.stream(WikiCelebrityType.values()).map(PathSupplier::getAsPath)
                .collect(Collectors.joining("|"));
            CELEBRITY_URL_REGEX = Pattern.compile(HOME_PAGE +
                "/(?<t>" + types + ")/m(?<id>\\d+)/((info|pic|news|comm)\\.html)?"
            );
            String albumTypes = Arrays.stream(WikiAlbumType.values()).map(PathSupplier::getAsPath)
                .collect(Collectors.joining("|"));
            ALBUM_URL_REGEX = Pattern.compile(HOME_PAGE +
                "/tuku/(?<t>" + albumTypes + ")/(?<id>\\d+)(_(?<i>\\d+))?\\.html");
        }
    }
}
