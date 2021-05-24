package wsg.tools.boot.dao.api;

/**
 * View of an episode from IMDb.
 *
 * @author Kingen
 * @since 2021/2/21
 */
public interface ImdbEpisodeView extends ImdbView {

    /**
     * Returns the identifier of the series that contain the episode
     *
     * @return the identifier of the series
     */
    String getSeriesId();
}
