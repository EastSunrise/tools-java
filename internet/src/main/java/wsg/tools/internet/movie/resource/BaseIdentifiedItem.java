package wsg.tools.internet.movie.resource;

import java.net.URL;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.view.SourceSupplier;
import wsg.tools.internet.movie.resource.view.IdentifierItem;

/**
 * A basic implementation of {@link IdentifierItem}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
abstract class BaseIdentifiedItem<E extends Enum<E>> extends BasicItem
    implements SourceSupplier, IdentifierItem<E> {

    private final E subtype;
    private final URL source;
    private final int id;
    private final String title;

    BaseIdentifiedItem(@Nonnull E subtype, @Nonnull URL source, int id, @Nonnull String title) {
        this.subtype = subtype;
        this.source = source;
        this.id = id;
        this.title = title;
    }

    @Override
    public E getSubtype() {
        return subtype;
    }

    @Override
    public URL getSource() {
        return source;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
