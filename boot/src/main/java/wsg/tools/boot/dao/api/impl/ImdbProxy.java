package wsg.tools.boot.dao.api.impl;

import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.site.ImdbRepository;

import javax.annotation.Nonnull;

/**
 * Proxy for {@code ImdbRepository} which isn't available now.
 *
 * @author Kingen
 * @since 2021/1/19
 */
public class ImdbProxy extends BaseSite implements ImdbRepository {

    public ImdbProxy() {
        super("IMDB Proxy", "localhost");
    }

    @Override
    public BaseImdbTitle getItemById(@Nonnull String imdbId) throws NotFoundException {
        throw new NotFoundException("Not found.");
    }
}
