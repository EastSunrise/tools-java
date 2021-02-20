package wsg.tools.internet.video.common;

import javax.annotation.Nullable;

/**
 * Ranged years of series.
 *
 * @author Kingen
 * @since 2020/9/5
 */
public class RangeYear {

    private final int start;
    private final Integer end;

    public RangeYear(int start) {
        this.start = start;
        this.end = null;
    }

    public RangeYear(int start, Integer end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    @Nullable
    public Integer getEnd() {
        return end;
    }
}
