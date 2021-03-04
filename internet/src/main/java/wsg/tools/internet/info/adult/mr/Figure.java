package wsg.tools.internet.info.adult.mr;

import lombok.Getter;
import wsg.tools.internet.info.adult.common.CupEnum;

/**
 * Data of BWH of a celebrity.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Getter
public class Figure {

    private final int bust;
    private final int waist;
    private final int hip;
    private CupEnum cup;

    Figure(int bust, int waist, int hip) {
        this.bust = bust;
        this.waist = waist;
        this.hip = hip;
    }

    void setCup(CupEnum cup) {
        this.cup = cup;
    }
}
