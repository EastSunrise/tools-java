package wsg.tools.internet.movie.resource.view;

import java.util.List;
import wsg.tools.internet.download.Link;

/**
 * Represents a supplier of links.
 *
 * @author Kingen
 * @since 2021/4/7
 */
public interface LinkSupplier {

    /**
     * Returns links.
     *
     * @return links
     */
    List<? extends Link> getLinks();
}
