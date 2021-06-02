package wsg.tools.internet.movie.resource;

import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.base.view.SubtypeSupplier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.TitleSupplier;

/**
 * An item with an integer identifier, a title, a cover and links, that also can be classified to a
 * subtype.
 *
 * @author Kingen
 * @since 2021/4/7
 */
public interface IdentifierItem<E extends Enum<E>>
    extends IntIdentifier, SubtypeSupplier<E>, TitleSupplier, CoverSupplier, LinkSupplier {

}
