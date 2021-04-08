package wsg.tools.internet.movie.resource.view;

import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.view.CoverSupplier;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.base.view.SubtypeSupplier;

/**
 * An item with an integer identifier, a title, a cover and links, that also can be classified to a
 * subtype.
 *
 * @author Kingen
 * @since 2021/4/7
 */
public interface IdentifierItem<E extends Enum<E>>
    extends SubtypeSupplier<E>, IntIdentifier, TitleSupplier, CoverSupplier, LinkSupplier {

}
