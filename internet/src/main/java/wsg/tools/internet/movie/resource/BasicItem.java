package wsg.tools.internet.movie.resource;

import java.util.Collections;
import java.util.List;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.support.InvalidResourceException;
import wsg.tools.internet.movie.resource.view.LinkSupplier;

/**
 * A basic item with a title and links of resources.
 *
 * @author Kingen
 * @since 2020/9/10
 */
class BasicItem implements LinkSupplier {

    private List<Link> links;
    private List<InvalidResourceException> exceptions;

    @Override
    public List<Link> getLinks() {
        return links;
    }

    void setLinks(List<Link> links) {
        this.links = Collections.unmodifiableList(links);
    }

    public List<InvalidResourceException> getExceptions() {
        return exceptions;
    }

    void setExceptions(List<InvalidResourceException> exceptions) {
        this.exceptions = Collections.unmodifiableList(exceptions);
    }
}
