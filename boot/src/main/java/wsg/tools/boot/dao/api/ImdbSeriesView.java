package wsg.tools.boot.dao.api;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * View of the series from IMDb.
 *
 * @author Kingen
 * @since 2021/2/21
 */
public interface ImdbSeriesView extends ImdbView {

    /**
     * Obtains count of seasons in the series
     *
     * @return the count
     */
    @Nonnull
    Integer getSeasonsCount();

    /**
     * Obtains all episodes of the series. Index of a given episode is
     * array[currentSeason-1][currentEpisode].
     * <p>
     * Ep0 may be included if exists. It also means that length of each array is at least 1 even if
     * all of the elements are null.
     *
     * @return list of the episodes
     */
    List<String[]> getEpisodes();
}
