package wsg.tools.boot.dao.api.intf;

/**
 * View of an episode from IMDb.
 *
 * @author Kingen
 * @since 2021/2/21
 */
public interface ImdbEpisodeView extends ImdbView {

    /**
     * Obtains identifier of the series that contain the episode
     *
     * @return the identifier
     */
    String getSeriesId();
}
