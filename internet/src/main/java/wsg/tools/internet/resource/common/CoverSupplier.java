package wsg.tools.internet.resource.common;

import java.net.URL;

/**
 * Supplies the cover of an item.
 *
 * @author Kingen
 * @since 2021/3/19
 */
public interface CoverSupplier {

    /**
     * Returns the cover of an item.
     *
     * @return the cover
     */
    URL getCover();
}
