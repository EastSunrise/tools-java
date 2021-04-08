package wsg.tools.internet.base.view;

import java.net.URL;
import wsg.tools.internet.base.EntityInterface;

/**
 * Supplies the cover of an item.
 *
 * @author Kingen
 * @since 2021/3/19
 */
@EntityInterface
public interface CoverSupplier {

    /**
     * Returns the cover of an item.
     *
     * @return the cover
     */
    URL getCover();
}
