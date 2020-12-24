package wsg.tools.internet.video.site;

import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;

import javax.annotation.Nonnull;

/**
 * Service of IMDb.com.
 *
 * @author Kingen
 * @since 2020/12/12
 */
public abstract class ImdbRepo extends BaseSite {

    public ImdbRepo(String name, String host) {
        super(name, host);
    }

    /**
     * Obtains an item by the given identifier.
     *
     * @param imdbId identifier, starting with 'tt'
     * @return info of the item
     * @throws NotFoundException if not found
     */
    public abstract BaseImdbTitle getItemById(@Nonnull String imdbId) throws NotFoundException;
}
