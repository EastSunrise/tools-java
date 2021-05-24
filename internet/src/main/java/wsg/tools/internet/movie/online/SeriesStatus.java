package wsg.tools.internet.movie.online;

/**
 * The status of a series.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public enum SeriesStatus {
    /**
     * to be continued
     */
    AIRING("连载中"),
    /**
     * finished
     */
    CONCLUDED("已完结"),
    /**
     * not released
     */
    PREPARING("未开播");

    private final String displayName;

    SeriesStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
