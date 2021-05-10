package wsg.tools.internet.common;

import java.net.URL;
import wsg.tools.internet.base.EntityProperty;

/**
 * Represents a supplier of the url of the cover.
 *
 * @author Kingen
 * @since 2021/3/19
 */
@EntityProperty
public interface CoverSupplier {

    /**
     * Returns the url of the cover.
     *
     * @return the url
     */
    URL getCoverURL();
}
