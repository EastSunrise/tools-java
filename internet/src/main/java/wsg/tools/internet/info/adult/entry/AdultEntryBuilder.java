package wsg.tools.internet.info.adult.entry;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.info.adult.common.Mosaic;

/**
 * Builds an {@link AbstractAdultEntry} with a map of information.
 *
 * @author Kingen
 * @since 2021/3/28
 */
public final class AdultEntryBuilder extends BasicAdultEntryBuilder {

    private final Map<String, String> info;
    private Mosaic mosaic;
    private Duration duration;
    private LocalDate release;
    private String director;
    private String producer;
    private String distributor;
    private String series;
    private List<String> tags;

    private AdultEntryBuilder(String code, Map<String, String> info) {
        super(code);
        this.info = info;
    }

    /**
     * Extracts the code of an adult entry from the given map or null if not found.
     */
    public static String getCode(@Nonnull Map<String, String> map) {
        return MapUtilsExt.getString(map, "品番", "番号");
    }

    /**
     * Initializes a builder of type {@link AbstractAdultEntry} by the given code and information
     * from the map.
     */
    public static AdultEntryBuilder builder(@Nonnull String code,
        @Nonnull Map<String, String> map) {
        return new AdultEntryBuilder(code, map);
    }

    /**
     * Builds an instance of {@link AmateurAdultEntry}, with a performer if found.
     *
     * @throws IllegalArgumentException if there are unhandled information left
     */
    @Override
    public AmateurAdultEntry amateur() {
        String performer = MapUtilsExt.getString(info, "出演", "出演者");
        return construct(code -> new AmateurAdultEntry(code, performer));
    }

    /**
     * Initializes a builder of type {@link AbstractAdultEntry} by the given code and information
     * from the map.
     *
     * @throws IllegalArgumentException if there are unhandled information left
     */
    public FormalAdultEntry formal(String separatorChars) {
        List<String> actresses = MapUtilsExt.getStringList(info, separatorChars, "女优", "演员");
        return construct(code -> new FormalAdultEntry(code, actresses));
    }

    /**
     * Verifies the consistency between current code and the code extracted from the info.
     *
     * @throws IllegalArgumentException if the two codes are conflict, ignoring if the info doesn't
     *                                  contain a code
     */
    public AdultEntryBuilder validateCode() {
        return (AdultEntryBuilder) validateCode(getCode(info));
    }

    /**
     * Removes all the given keys from the information map.
     *
     * @param keys array of keys to be removed, each key must not be null
     * @throws NullPointerException if any key is null
     */
    public AdultEntryBuilder ignore(@Nonnull String... keys) {
        for (String key : keys) {
            info.remove(key);
        }
        return this;
    }

    /**
     * Removes all remaining keys from the information map.
     */
    public AdultEntryBuilder ignoreAllRemaining() {
        info.clear();
        return this;
    }

    @Override
    public AdultEntryBuilder title(@Nonnull String title) {
        return (AdultEntryBuilder) super.title(title);
    }

    @Override
    public AdultEntryBuilder description(@Nonnull String description) {
        return (AdultEntryBuilder) super.description(description);
    }

    @Override
    public AdultEntryBuilder images(List<URL> images) {
        return (AdultEntryBuilder) super.images(images);
    }

    public AdultEntryBuilder title() {
        return title(MapUtilsExt.getString(info, "名称"));
    }

    public AdultEntryBuilder mosaic() {
        mosaic = MapUtilsExt.getValue(info,
            s -> EnumUtilExt.valueOfTitle(Mosaic.class, s, false), "是否有码");
        return this;
    }

    public AdultEntryBuilder duration() {
        duration = MapUtilsExt.getValue(info, s -> {
            String dur = RegexUtils.matchesOrElseThrow(Lazy.DURATION_REGEX, s).group("d");
            return dur == null ? null : Duration.ofMinutes(Integer.parseInt(dur));
        }, "时长", "收录时间", "収录时间", "収録时间", "収録時間", "播放时间");
        return this;
    }

    public AdultEntryBuilder release() {
        release = MapUtilsExt.getValue(info, s -> {
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.RELEASE_REGEX, s);
            String year = matcher.group("y");
            if (year == null) {
                return null;
            }
            return LocalDate.of(Integer.parseInt(year), Integer.parseInt(matcher.group("m")),
                Integer.parseInt(matcher.group("d")));
        }, "配信开始日", "発売日", "发行时间", "配信開始日", "公開日");
        return this;
    }

    public AdultEntryBuilder director() {
        director = MapUtilsExt.getString(info, "导演");
        return this;
    }

    public AdultEntryBuilder producer() {
        producer = MapUtilsExt.getString(info, "制作商", "制造商", "制造厂", "メーカー", "メーカー名");
        return this;
    }

    public AdultEntryBuilder distributor() {
        distributor = MapUtilsExt.getString(info, "厂商", "发行商", "レーベル");
        return this;
    }

    public AdultEntryBuilder series() {
        series = MapUtilsExt.getString(info, "シリーズ", "系列", "影片系列");
        return this;
    }

    public AdultEntryBuilder tags(String separatorChars) {
        List<String> list = MapUtilsExt.getStringList(info, separatorChars, "ジャンル");
        if (CollectionUtils.isNotEmpty(list)) {
            this.tags = Collections.unmodifiableList(list);
        }
        return this;
    }

    private <T extends AbstractAdultEntry> T construct(@Nonnull Function<String, T> constructor) {
        if (!info.isEmpty()) {
            String unhandled = String.join(", ", info.keySet());
            throw new IllegalArgumentException("Unhandled info: " + unhandled);
        }
        T entry = constructBasic(constructor);
        entry.setMosaic(mosaic);
        entry.setDuration(duration);
        entry.setRelease(release);
        entry.setDirector(director);
        entry.setProducer(producer);
        entry.setDistributor(distributor);
        entry.setSeries(series);
        entry.setTags(tags);
        return entry;
    }

    private static class Lazy {

        private static final Pattern DURATION_REGEX = Pattern.compile("(?<d>\\d+)?(min|分|分钟)");
        private static final Pattern RELEASE_REGEX = Pattern.compile(
            "(月額▶)?((?<y>\\d{4})([-/])(?<m>\\d{2})\\4(?<d>\\d{2})|--)"
                + "( \\((DVD|VHS|BD) (セル版|レンタル版|セルorレンタル|记载无し)?\\))?");
    }
}
