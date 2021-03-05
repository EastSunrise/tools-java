package wsg.tools.internet.movie.common.enums;

/**
 * Types of creators on {@link wsg.tools.internet.movie.douban.DoubanSite}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
public enum DoubanCreator {
    /**
     * Celebrities/Authors/Musicians
     */
    CELEBRITY("celebrities"),
    AUTHOR("authors"),
    MUSICIAN("musicians");

    private final String plurality;

    DoubanCreator(String plurality) {
        this.plurality = plurality;
    }

    public String getPlurality() {
        return plurality;
    }
}
