package wsg.tools.internet.movie.omdb;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import wsg.tools.internet.base.page.FixedSizePageReq;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * The interface provides methods to query IMDb movies from a open api.
 *
 * @author Kingen
 * @see <a href="https://www.omdbapi.com/">OMDb API</a>
 * @since 2021/5/21
 */
public interface OmdbApi {

    /**
     * Returns the key to access to the api.
     *
     * @return the key
     */
    String getApikey();

    /**
     * Searches movies by the specified arguments
     *
     * @param keyword     the keyword to search
     * @param req         pagination information
     * @param optionalReq optional arguments
     * @return searched movies
     * @throws NotFoundException        if no movies are found
     * @throws OtherResponseException   if an unexpected error occurs
     * @throws IllegalArgumentException if the keyword is blank
     */
    OmdbPageResult searchMovies(String keyword, @Nonnull FixedSizePageReq req,
        @Nullable OmdbOptionalReq optionalReq) throws NotFoundException, OtherResponseException;

    /**
     * Retrieves a movie of the specified id or title.
     *
     * @param req         request that contains required arguments
     * @param optionalReq optional arguments
     * @return the movie
     * @throws NotFoundException      if the movie is not found
     * @throws OtherResponseException if an unexpected error occurs
     */
    AbstractOmdbMovie findMovie(@Nonnull OmdbReq req, @Nullable OmdbOptionalReq optionalReq)
        throws NotFoundException, OtherResponseException;

    /**
     * Retrieves the specified season.
     *
     * @param req    request that contains required arguments
     * @param season the number of the season, starting with 1
     * @return the season
     * @throws NotFoundException        if the season is not found
     * @throws OtherResponseException   if an unexpected error occurs
     * @throws IllegalArgumentException if the number of the season is not positive
     */
    OmdbSeason findSeason(@Nonnull OmdbReq req, int season)
        throws NotFoundException, OtherResponseException;

    /**
     * Retrieves the specified episode.
     *
     * @param req     request that contains required arguments
     * @param season  the season to which the episode belongs, starting with 1
     * @param episode the number of the episode, starting with 1
     * @return the episode
     * @throws NotFoundException        if the episode is not found
     * @throws OtherResponseException   if an unexpected error occurs
     * @throws IllegalArgumentException if any number is not positive
     */
    OmdbEpisode findEpisode(@Nonnull OmdbReq req, int season, int episode)
        throws NotFoundException, OtherResponseException;
}
