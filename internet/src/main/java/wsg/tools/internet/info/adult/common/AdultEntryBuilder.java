package wsg.tools.internet.info.adult.common;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.regex.RegexUtils;

/**
 * Builder for {@link AdultEntry} and its implementations
 *
 * @author Kingen
 * @since 2021/3/3
 */
public class AdultEntryBuilder {

    private static final Pattern RELEASE_REGEX = Pattern.compile(
        "(月額▶)?((?<y>\\d{4})([-/])(?<m>\\d{2})\\4(?<d>\\d{2})|--)"
            + "( \\((DVD|VHS|BD) (セル版|レンタル版|セルorレンタル|记载无し)?\\))?");
    private static final Pattern DURATION_REGEX = Pattern.compile("(?<d>\\d+)?(min|分|分钟)");

    private final AdultEntry entry;

    private AdultEntryBuilder(AdultEntry entry) {
        this.entry = entry;
    }

    public static String getCode(@Nonnull Map<String, String> map) {
        return MapUtilsExt.getString(map, "品番", "番号");
    }

    /**
     * Obtains an instance of {@link AdultEntry} only with the code.
     */
    public static AdultEntryBuilder basic(@Nonnull String code) {
        return new AdultEntryBuilder(new AdultEntry(code));
    }

    /**
     * Initializes a builder with an {@link AdultEntry} without any performer or actress.
     */
    public static AdultEntryMapBuilder entry(@Nonnull Map<String, String> map,
        @Nonnull String code) {
        return new AdultEntryMapBuilder(new AdultEntry(code), map);
    }

    /**
     * Initializes a builder with an adult entry, a {@link AmateurAdultEntry} if any performer
     * found, or an {@link AdultEntry} without any performer
     */
    public static AdultEntryMapBuilder amateur(@Nonnull Map<String, String> map,
        @Nonnull String code) {
        map.remove("商品発売日");
        String performer = MapUtilsExt.getString(map, "出演", "出演者");
        if (performer == null) {
            return entry(map, code);
        }
        return new AdultEntryMapBuilder(new AmateurAdultEntry(code, performer), map);
    }

    /**
     * Initializes a builder with an adult entry, a {@link FormalAdultEntry} if any actress found,
     * or an {@link AdultEntry} without any actress
     */
    public static AdultEntryMapBuilder formal(@Nonnull Map<String, String> map,
        @Nonnull String code, String separatorChars) {
        List<String> actresses = MapUtilsExt.getStringList(map, separatorChars, "女优", "演员");
        if (actresses == null) {
            return entry(map, code);
        }
        return new AdultEntryMapBuilder(new FormalAdultEntry(code, actresses), map);
    }

    public AdultEntryBuilder title(@Nonnull String title) {
        entry.setTitle(title);
        return this;
    }

    public AdultEntryBuilder description(@Nonnull String description) {
        entry.setDescription(description);
        return this;
    }

    public AdultEntryBuilder images(@Nonnull List<URL> images) {
        entry.setImages(images);
        return this;
    }

    public AdultEntry build() {
        return entry;
    }

    AdultEntry getEntry() {
        return entry;
    }

    public static final class AdultEntryMapBuilder extends AdultEntryBuilder {

        private final Map<String, String> info;

        private AdultEntryMapBuilder(AdultEntry entry, Map<String, String> info) {
            super(entry);
            this.info = info;
        }

        /**
         * Verifies the consistency between the code of the {@link #entry} and the code extracted
         * from the info.
         *
         * @throws IllegalArgumentException if the two codes are conflict, ignoring if the info
         *                                  doesn't contain a code
         */
        public AdultEntryMapBuilder validateCode() {
            String code = getCode(info);
            if (code == null || code.equalsIgnoreCase(getEntry().getCode())) {
                return this;
            }
            throw new IllegalArgumentException("Code is not found");
        }

        public AdultEntryMapBuilder title() {
            title(MapUtilsExt.getString(info, "名称"));
            return this;
        }

        public AdultEntryMapBuilder mosaic() {
            getEntry().setMosaic(MapUtilsExt.getValue(info,
                s -> EnumUtilExt.valueOfTitle(s, Mosaic.class, false), "是否有码"));
            return this;
        }

        public AdultEntryMapBuilder duration() {
            Duration duration = MapUtilsExt.getValue(info, s -> {
                String dur = RegexUtils.matchesOrElseThrow(DURATION_REGEX, s).group("d");
                return dur == null ? null : Duration.ofMinutes(Integer.parseInt(dur));
            }, "时长", "收录时间", "収录时间", "収録时间", "収録時間", "播放时间");
            getEntry().setDuration(duration);
            return this;
        }

        public AdultEntryMapBuilder release() {
            LocalDate release = MapUtilsExt.getValue(info, s -> {
                Matcher matcher = RegexUtils.matchesOrElseThrow(RELEASE_REGEX, s);
                String year = matcher.group("y");
                if (year == null) {
                    return null;
                }
                return LocalDate.of(Integer.parseInt(year), Integer.parseInt(matcher.group("m")),
                    Integer.parseInt(matcher.group("d")));
            }, "配信开始日", "発売日", "发行时间", "配信開始日", "公開日");
            getEntry().setRelease(release);
            return this;
        }

        public AdultEntryMapBuilder director() {
            getEntry().setDirector(MapUtilsExt.getString(info, "导演"));
            return this;
        }

        public AdultEntryMapBuilder producer() {
            getEntry()
                .setProducer(MapUtilsExt.getString(info, "制作商", "制造商", "制造厂", "メーカー", "メーカー名"));
            return this;
        }

        public AdultEntryMapBuilder distributor() {
            getEntry().setDistributor(MapUtilsExt.getString(info, "厂商", "发行商", "レーベル"));
            return this;
        }

        public AdultEntryMapBuilder series() {
            getEntry().setSeries(MapUtilsExt.getString(info, "シリーズ", "系列", "影片系列"));
            return this;
        }

        public AdultEntryMapBuilder tags(String separatorChars) {
            getEntry().setTags(MapUtilsExt.getStringList(info, separatorChars, "ジャンル"));
            return this;
        }

        /**
         * Validates whether there are unhandled information left.
         */
        @Override
        public AdultEntry build() {
            if (!info.isEmpty()) {
                String unhandled = String.join(", ", info.keySet());
                throw new IllegalArgumentException("Unhandled info: " + unhandled);
            }
            return super.build();
        }
    }
}
