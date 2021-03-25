package wsg.tools.internet.resource.movie;

import java.util.Objects;
import wsg.tools.common.lang.AssertUtils;
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

    IdentifiedItem(int id, String url, E subtype) {
        this.id = id;
        this.url = AssertUtils.requireNotBlank(url, "the url of an item");
        this.subtype = Objects.requireNonNull(subtype, "the subtype of an item");
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
