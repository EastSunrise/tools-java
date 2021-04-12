package wsg.tools.internet.info.adult.wiki;

import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.base.view.SubtypeSupplier;

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
