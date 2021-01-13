package wsg.tools.internet.video.site;

import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;

import javax.annotation.Nonnull;

/**
 * Service of IMDb.com.
 *
 * @author Kingen
 * @since 2020/12/12
 */
public interface ImdbRepository {

    /**
     * Obtains an item by the given identifier.
     *
     * @param imdbId identifier, starting with 'tt'
     * @return info of the item
     * @throws NotFoundException if not found
     */
    BaseImdbTitle getItemById(@Nonnull String imdbId) throws NotFoundException;
}
