package wsg.tools.internet.info.adult.common;

/**
 * The measurements of a woman, including a cup.
 *
 * @author Kingen
 * @since 2021/3/15
 */
public class Measurements {

    private final int bust;
    private Integer waist;
    private Integer hip;
    private CupEnum cup;

    public Measurements(int bust) {
        this.bust = bust;
    }

    public int getBust() {
        return bust;
    }

    public int getWaist() {
        return waist;
    }

    public void setWaist(Integer waist) {
        this.waist = waist;
    }

    public int getHip() {
        return hip;
    }

    public void setHip(Integer hip) {
        this.hip = hip;
    }

    public CupEnum getCup() {
        return cup;
    }

    public void setCup(CupEnum cup) {
        this.cup = cup;
    }
}
