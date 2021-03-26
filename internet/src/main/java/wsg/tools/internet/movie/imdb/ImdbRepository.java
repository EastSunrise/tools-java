package wsg.tools.internet.movie.imdb;

import javax.annotation.Nonnull;
import wsg.tools.internet.base.repository.Repository;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * Service of IMDb.com.
 *
 * @author Kingen
 * @since 2020/12/12
 */
@FunctionalInterface
public interface ImdbRepository<T extends ImdbIdentifier> extends Repository<String, T> {

    /**
     * Obtains an item by the given identifier.
     *
     * @param imdbId identifier, starting with 'tt'
     * @return the item with detailed info
     * @throws NullPointerException   if the specified identifier is null
     * @throws NotFoundException      if the item is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    @Nonnull
    @Override
    T findById(String imdbId) throws NotFoundException, OtherResponseException;
}
