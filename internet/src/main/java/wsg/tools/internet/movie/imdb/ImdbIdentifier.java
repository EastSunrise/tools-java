package wsg.tools.internet.movie.imdb;

import wsg.tools.internet.base.EntityProperty;

/**
 * Supplies the identifier on IMDb.
 *
 * @author Kingen
 * @since 2020/11/4
 */
@EntityProperty
public interface ImdbIdentifier {

    /**
     * Returns the identifier on IMDb.
     *
     * @return the identifier
     */
    String getImdbId();
}
