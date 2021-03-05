package wsg.tools.internet.info.adult;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.info.adult.common.Mosaic;

/**
 * Utility for adult entries.
 *
 * @author Kingen
 * @since 2021/3/3
 */
@UtilityClass
public class AdultEntryUtils {

    private final Pattern DURATION_REGEX = Pattern.compile("(?<d>\\d+)?(min|分|分钟)");
    private final Pattern RELEASE_REGEX = Pattern.compile(
        "(月額▶)?((?<y>\\d{4})([-/])(?<m>\\d{2})\\4(?<d>\\d{2})|--)( \\((DVD|VHS|BD) (セル版|レンタル版|セルorレンタル|记载无し)?\\))?");

    /**
     * Constructs an adult entry with all properties based on the given map except cover.
     *
     * @return the adult entry, {@code null} if the code is unavailable
     */
    public AdultEntry getAdultEntry(@Nonnull Map<String, String> map, @Nonnull String cover,
        String separatorChars) {
        String code = getString(map, "品番", "番号");
        if (code == null) {
            return null;
        }
        return getAdultEntry(map, code, cover, separatorChars);
    }

    /**
     * Constructs an adult entry based on the given code and cover, with other properties extracted
     * from the given map.
     *
     * @return the adult entry
     */
    public AdultEntry getAdultEntry(@Nonnull Map<String, String> map, @Nonnull String code,
        @Nonnull String cover,
        String separatorChars) {
        String title = getString(map, "名称");
        Mosaic mosaic = getValue(map, s -> EnumUtilExt.deserializeTitle(s, Mosaic.class, false),
            "是否有码");
        Duration duration = getValue(map, s -> {
            String d = RegexUtils.matchesOrElseThrow(DURATION_REGEX, s).group("d");
            return d == null ? null : Duration.ofMinutes(Integer.parseInt(d));
        }, "时长", "收录时间", "収录时间", "収録时间", "収録時間", "播放时间");
        LocalDate release = getValue(map, s -> {
            Matcher m = RegexUtils.matchesOrElseThrow(RELEASE_REGEX, s);
            String year = m.group("y");
            if (year == null) {
                return null;
            }
            return LocalDate.of(Integer.parseInt(year), Integer.parseInt(m.group("m")),
                Integer.parseInt(m.group("d")));
        }, "配信开始日", "発売日", "发行时间", "配信開始日", "公開日");
        String director = getString(map, "导演");
        String producer = getString(map, "制作商", "制造商", "制造厂", "メーカー");
        String distributor = getString(map, "厂商", "发行商", "レーベル");
        String series = getString(map, "系列", "影片系列", "シリーズ");
        List<String> tags = getStringList(map, separatorChars, "类别", "ジャンル");

        String performer = getString(map, "出演", "出演者");
        if (performer != null) {
            return new SingleAdultEntry(code, cover, performer, title, mosaic, duration, release,
                director, producer,
                distributor, series, tags);
        }
        List<String> performers = getStringList(map, separatorChars, "演员", "女优");
        if (CollectionUtils.isEmpty(performers)) {
            return new AdultEntry(code, cover, title, mosaic, duration, release, director, producer,
                distributor,
                series, tags);
        }
        if (performers.size() == 1) {
            return new SingleAdultEntry(code, cover, performers.get(0), title, mosaic, duration,
                release, director,
                producer, distributor, series, tags);
        }
        return new MultiAdultEntry(code, cover, performers, title, mosaic, duration, release,
            director, producer,
            distributor, series, tags);
    }

    public List<String> getStringList(@Nonnull Map<String, String> map, String separatorChars,
        String... keys) {
        return getValues(map, Function.identity(), separatorChars, keys);
    }

    public <T> List<T> getValues(@Nonnull Map<String, String> map,
        @Nonnull Function<? super String, T> function,
        String separatorChars, String... keys) {
        return getValue(map,
            s -> Arrays.stream(StringUtils.split(s, separatorChars)).map(function)
                .collect(Collectors.toList()), keys);
    }

    public String getString(@Nonnull Map<String, String> map, String... keys) {
        return getValue(map, Function.identity(), keys);
    }

    public <T> T getValueIfMatched(Map<String, String> map, Pattern pattern,
        Function<? super Matcher, T> function,
        String... keys) {
        return getValue(map, text -> {
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                return function.apply(matcher);
            }
            return null;
        }, keys);
    }

    /**
     * Obtains a specific value from the given map.
     *
     * @param map      the map to query
     * @param function function transfer a string to the target object
     * @param keys     keys to query the map
     * @param <T>      type of returned value
     * @return target value, or {@code null} if none key is found in the map
     */
    public <T> T getValue(@Nonnull Map<String, String> map,
        @Nonnull Function<? super String, T> function,
        String... keys) {
        for (String key : keys) {
            String value = map.remove(key);
            if (value != null) {
                return function.apply(value);
            }
        }
        return null;
    }
}
