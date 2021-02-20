package wsg.tools.internet.video.site.imdb;

import org.apache.http.client.HttpResponseException;

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
     * @throws HttpResponseException if an error occurs.
     */
    T getItemById(@Nonnull String imdbId) throws HttpResponseException;
}
