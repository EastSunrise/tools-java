package wsg.tools.internet.video.site.imdb.pojo.info;

import lombok.Getter;

/**
 * Info of year.
 *
 * @author Kingen
 * @since 2020/9/5
 */
public class YearInfo {

    @Getter
    private final int start;
    private final Integer end;

    public YearInfo(int start) {
        this.start = start;
        this.end = null;
    }

    public YearInfo(int start, Integer end) {
        this.start = start;
        this.end = end;
    }
}
