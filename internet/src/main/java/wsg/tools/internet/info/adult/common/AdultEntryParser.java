package wsg.tools.internet.info.adult.common;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.common.util.MapUtilsExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.info.adult.view.ActressSupplier;
import wsg.tools.internet.info.adult.view.AmateurSupplier;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.SerialNumSupplier;
import wsg.tools.internet.info.adult.view.Tagged;

/**
 * This class helps extract information of adult entries from a map.
 *
 * @author Kingen
 * @since 2021/4/7
 */
public final class AdultEntryParser
    implements SerialNumSupplier, AmateurSupplier, DurationSupplier {

    private final Map<String, String> info;

    private AdultEntryParser(Map<String, String> info) {
        this.info = info;
    }

    @Nonnull
    @Contract(value = "_ -> new", pure = true)
    public static AdultEntryParser create(@Nonnull Map<String, String> info) {
        return new AdultEntryParser(info);
    }

    @Override
    public String getSerialNum() {
        return MapUtilsExt.getString(info, "品番", "番号");
    }

    /**
     * Verifies the consistency between the serial number extracted from the info and the supplied
     * one.
     *
     * @throws IllegalArgumentException if the two serial numbers are conflict, ignoring if the info
     *                                  doesn't contain a serial number
     */
    public void verifySerialNumber(SerialNumSupplier supplier) {
        String thisNum = getSerialNum();
        if (thisNum == null) {
            return;
        }
        String serialNum = supplier.getSerialNum();
        if (Objects.equals(thisNum, serialNum)) {
            return;
        }
        String message = String
            .format("The two serial numbers are conflict: '%s' and '%s'", thisNum, serialNum);
        throw new IllegalArgumentException(message);
    }

    @Override
    public String getPerformer() {
        return MapUtilsExt.getString(info, "出演", "出演者");
    }

    /**
     * Extracts actresses from the map.
     *
     * @see ActressSupplier#getActresses()
     */
    @Nonnull
    public List<String> getActresses(String separatorChars) {
        return MapUtilsExt.getStringList(info, separatorChars, "女优", "演员");
    }

    public String getTitle() {
        return MapUtilsExt.getString(info, "名称");
    }

    public Boolean getMosaic() {
        String text = MapUtilsExt.getString(info, "是否有码");
        if (text == null) {
            return null;
        }
        return "有码".equals(text);
    }

    @Override
    public Duration getDuration() {
        return MapUtilsExt.getValue(info, s -> {
            String dur = RegexUtils.matchesOrElseThrow(Lazy.DURATION_REGEX, s).group("d");
            return dur == null ? null : Duration.ofMinutes(Integer.parseInt(dur));
        }, "时长", "收录时间", "収录时间", "収録时间", "収録時間", "播放时间");
    }

    public LocalDate getPublish() {
        return MapUtilsExt.getValue(info, s -> {
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.RELEASE_REGEX, s);
            String year = matcher.group("y");
            if (year == null) {
                return null;
            }
            return LocalDate.of(Integer.parseInt(year), Integer.parseInt(matcher.group("m")),
                Integer.parseInt(matcher.group("d")));
        }, "配信开始日", "発売日", "发行时间", "配信開始日", "公開日");
    }

    public String getDirector() {
        return MapUtilsExt.getString(info, "导演");
    }

    public String getProducer() {
        return MapUtilsExt.getString(info, "制作商", "制造商", "制造厂", "メーカー", "メーカー名");
    }

    public String getDistributor() {
        return MapUtilsExt.getString(info, "厂商", "发行商", "レーベル");
    }

    public String getSeries() {
        return MapUtilsExt.getString(info, "シリーズ", "系列", "影片系列");
    }

    /**
     * Extracts tags from the map.
     *
     * @see Tagged#getTags()
     */
    @Nonnull
    public Set<String> getTags(String separatorChars) {
        return new HashSet<>(MapUtilsExt.getStringList(info, separatorChars, "ジャンル", "类别"));
    }

    /**
     * Checks whether there is any unhandled information left.
     *
     * @param keys keys array of keys to be ignored when checking, each key must not be null
     * @throws IllegalArgumentException if there is any unexpected information unhandled
     */
    public void check(@Nonnull String... keys) {
        for (String key : keys) {
            info.remove(key);
        }
        if (!info.isEmpty()) {
            String unhandled = String.join(", ", info.keySet());
            throw new IllegalArgumentException("Unhandled info: " + unhandled);
        }
    }

    private static class Lazy {

        private static final Pattern DURATION_REGEX = Pattern.compile("(?<d>\\d+)?(min|分|分钟)");
        private static final Pattern RELEASE_REGEX = Pattern.compile(
            "(月額▶)?((?<y>\\d{4})([-/])(?<m>\\d{2})\\4(?<d>\\d{2})|--)"
                + "( \\((DVD|VHS|BD) (セル版|レンタル版|セルorレンタル|记载无し)?\\))?");
    }
}
