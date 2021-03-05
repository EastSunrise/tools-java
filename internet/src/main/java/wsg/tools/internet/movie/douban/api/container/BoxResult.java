package wsg.tools.internet.movie.douban.api.container;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.movie.douban.api.pojo.SimpleSubject;

/**
 * Result of subjects with box info.
 *
 * @author Kingen
 * @since 2020/8/2
 */
@Getter
public class BoxResult {

    private String title;
    private RangeDate date;
    private List<BoxSubject> subjects;

    @Getter
    public static final class RangeDate {

        private static final Pattern DATE_REGEX = Pattern
            .compile("(?<s>\\d+月\\d+日) - (?<e>\\d+月\\d+日)");
        private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("M月d日").withLocale(Locale.CHINESE);

        private final MonthDay start;
        private final MonthDay end;

        private RangeDate(MonthDay start, MonthDay end) {
            this.start = start;
            this.end = end;
        }

        public static RangeDate of(String text) {
            if (text == null) {
                return null;
            }
            Matcher matcher = RegexUtils.matchesOrElseThrow(DATE_REGEX, text);
            return new RangeDate(MonthDay.parse(matcher.group("s"), DATE_FORMATTER),
                MonthDay.parse(matcher.group("e"), DATE_FORMATTER));
        }
    }

    @Getter
    private static class BoxSubject {

        private Long box;
        @JsonProperty("new")
        private Boolean newMovie;
        private Integer rank;
        private SimpleSubject subject;
    }
}
