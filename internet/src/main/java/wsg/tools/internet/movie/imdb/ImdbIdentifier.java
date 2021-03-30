package wsg.tools.internet.movie.imdb;

import wsg.tools.internet.base.EntityInterface;

/**
 * Supplies the identifier on IMDb.
 *
 * @author Kingen
 * @since 2020/11/4
 */
@EntityInterface
public interface ImdbIdentifier {

    /**
     * Returns the identifier on IMDb.
     *
     * @return the identifier
     */
    String getImdbId();
}
