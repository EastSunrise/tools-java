package wsg.tools.internet.info.adult.wiki;

import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.view.IntIdentifier;

/**
 * An index pointing to a {@link WikiAlbum}.
 *
 * @author Kingen
 * @see WikiCelebrity#getAlbums()
 * @since 2021/2/26
 */
public interface WikiAlbumIndex extends IntIdentifier, TitleSupplier {

    /**
     * Returns the type of the album.
     *
     * @return the type
     */
    WikiAlbumType getType();
}
