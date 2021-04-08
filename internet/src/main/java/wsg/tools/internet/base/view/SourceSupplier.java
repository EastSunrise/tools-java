package wsg.tools.internet.base.view;

import java.net.URL;

/**
 * Represents a supplier of the source url of the entity.
 *
 * @author Kingen
 * @since 2021/4/7
 */
public interface SourceSupplier {

    /**
     * Returns the source url of the entity.
     *
     * @return the source url
     */
    URL getSource();
}
