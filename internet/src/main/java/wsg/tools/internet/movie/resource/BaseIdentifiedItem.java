package wsg.tools.internet.movie.resource;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.support.InvalidResourceException;

/**
 * A basic implementation of {@link IdentifierItem}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
abstract class BaseIdentifiedItem<E extends Enum<E>> implements IdentifierItem<E> {

    private final int id;
    private final E subtype;
    private final String title;
    private final List<Link> links;
    private final List<InvalidResourceException> exceptions;
    private URL cover;

    BaseIdentifiedItem(int id, @Nonnull E subtype, @Nonnull String title, List<Link> links,
        List<InvalidResourceException> exceptions) {
        this.id = id;
        this.subtype = subtype;
        this.title = title;
        this.links = links;
        this.exceptions = exceptions;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public E getSubtype() {
        return subtype;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<? extends Link> getLinks() {
        return links;
    }

    public List<InvalidResourceException> getExceptions() {
        return exceptions;
    }

    @Override
    public URL getCoverURL() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = Objects.requireNonNull(cover, "the cover of an item");
    }
}
