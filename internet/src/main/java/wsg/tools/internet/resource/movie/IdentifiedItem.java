package wsg.tools.internet.resource.movie;

import javax.annotation.Nonnull;
import wsg.tools.common.lang.IntIdentifier;
import wsg.tools.internet.resource.common.SubtypeSupplier;

/**
 * An item with an identifier.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class IdentifiedItem<E extends Enum<E>> extends BasicItem
    implements IntIdentifier, SubtypeSupplier<E> {

    private final int id;
    private final String url;
    private final E subtype;

    IdentifiedItem(int id, @Nonnull String url, @Nonnull E subtype) {
        this.id = id;
        this.url = url;
        this.subtype = subtype;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public E getSubtype() {
        return subtype;
    }
}
