package wsg.tools.internet.video.entity.imdb.info;

import lombok.Getter;

import java.time.Year;

/**
 * Info of year.
 *
 * @author Kingen
 * @since 2020/9/5
 */
public class YearInfo {

    @Getter
    private final Year start;
    private final Year end;

    public YearInfo(Year start) {
        this.start = start;
        this.end = null;
    }

    public YearInfo(Year start, Year end) {
        this.start = start;
        this.end = end;
    }
}
