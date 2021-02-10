package wsg.tools.internet.resource.item.intf;

import java.time.temporal.Temporal;

/**
 * Supply update time of the item.
 *
 * @author Kingen
 * @since 2021/2/4
 */
public interface UpdateTimeSupplier<T extends Temporal> {

    /**
     * Obtains update time.
     *
     * @return update time
     */
    T getUpdateTime();
}
