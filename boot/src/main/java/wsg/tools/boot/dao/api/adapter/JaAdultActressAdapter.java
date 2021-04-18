package wsg.tools.boot.dao.api.adapter;

import javax.annotation.Nonnull;
import wsg.tools.boot.pojo.entity.adult.JaAdultActressEntity;
import wsg.tools.internet.info.adult.view.AlbumSupplier;

/**
 * Adapts an entity to a {@link JaAdultActressEntity}.
 *
 * @author Kingen
 * @since 2021/4/16
 */
public interface JaAdultActressAdapter extends AlbumSupplier {

    /**
     * Transfers the entity to a {@link JaAdultActressEntity} except images.
     *
     * @return the result of transferring
     */
    @Nonnull
    JaAdultActressEntity toActress();
}
