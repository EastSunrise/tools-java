package wsg.tools.internet.video.site.imdb;

import wsg.tools.internet.base.exception.NotFoundException;

import javax.annotation.Nonnull;

/**
 * Service of IMDb.com.
 *
 * @author Kingen
 * @since 2020/12/12
 */
public interface ImdbRepository<T extends ImdbIdentifier> {

    /**
     * Obtains an item by the given identifier.
     *
     * @param imdbId identifier, starting with 'tt'
     * @return info of the item
     * @throws NotFoundException if not found
     */
    T getItemById(@Nonnull String imdbId) throws NotFoundException;
}
