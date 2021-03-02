package wsg.tools.internet.video.site.imdb;

import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.base.intf.Repository;

import javax.annotation.Nonnull;

/**
 * Service of IMDb.com.
 *
 * @author Kingen
 * @since 2020/12/12
 */
public interface ImdbRepository<T extends ImdbIdentifier> extends Repository<String, T> {

    /**
     * Obtains an item by the given identifier.
     *
     * @param imdbId identifier, starting with 'tt'
     * @return the item with detailed info
     * @throws HttpResponseException if an error occurs.
     */
    @Override
    T findById(@Nonnull String imdbId) throws HttpResponseException;
}
