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

    public Measurements(int bust) {
        this.bust = bust;
    }

    public Measurements(int bust, int waist, int hip) {
        this.bust = bust;
        this.waist = waist;
        this.hip = hip;
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
}
