package wsg.tools.internet.info.adult.wiki;

import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.base.view.SubtypeSupplier;
import wsg.tools.internet.common.TitleSupplier;

/**
 * An index pointing to a {@link WikiAlbum}.
 *
 * @author Kingen
 * @see WikiCelebrity#getAlbums()
 * @since 2021/2/26
 */
public interface WikiAlbumIndex
    extends IntIdentifier, SubtypeSupplier<WikiAlbumType>, TitleSupplier {

}
