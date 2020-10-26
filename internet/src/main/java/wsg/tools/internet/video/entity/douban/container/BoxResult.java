package wsg.tools.internet.video.entity.douban.container;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.video.entity.douban.pojo.SimpleSubject;

import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Result of subjects with box info.
 *
 * @author Kingen
 * @since 2020/8/2
 */
@Setter
@Getter
public class BoxResult {

    private static final Pattern DATE_REGEX = Pattern.compile("(\\d+月\\d+日) - (\\d+月\\d+日)");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M月d日").withLocale(Locale.CHINESE);

    private String title;
    private MonthDay start;
    private MonthDay end;
    private List<BoxSubject> subjects;

    @JsonProperty("date")
    public void setDate(String date) {
        if (StringUtils.isBlank(date)) {
            return;
        }
        Matcher matcher = AssertUtils.matches(DATE_REGEX, date);
        this.setStart(MonthDay.parse(matcher.group(1), DATE_FORMATTER));
        this.setEnd(MonthDay.parse(matcher.group(2), DATE_FORMATTER));
    }

    @Getter
    @Setter
    public static class BoxSubject {
        private Long box;
        @JsonProperty("new")
        private Boolean newMovie;
        private Integer rank;
        private SimpleSubject subject;
    }
}
