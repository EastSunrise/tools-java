package wsg.tools.internet.movie.omdb;

import java.time.LocalDate;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;

/**
 * Represents an episode from OMDb with episode-related information.
 *
 * @author Kingen
 * @since 2021/5/21
 */
public interface OmdbEpisodeIndex extends ImdbIdentifier {

    /**
     * Returns the title of the episode.
     *
     * @return the title
     */
    String getTitle();

    /**
     * Returns the number of the episode.
     *
     * @return the number of the episode
     */
    int getCurrentEpisode();

    /**
     * Returns the release date of the episode.
     *
     * @return the release date
     */
    LocalDate getRelease();

    /**
     * Returns the rating of the episode.
     *
     * @return the rating
     */
    Double getImdbRating();
}
