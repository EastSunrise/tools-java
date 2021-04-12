package wsg.tools.internet.common;

import java.time.temporal.Temporal;

/**
 * Represents a supplier of a temporal object.
 *
 * @author Kingen
 * @since 2021/4/10
 */
public interface UpdateTemporalSupplier<T extends Temporal> {

    /**
     * Returns the temporal object.
     *
     * @return the temporal object
     */
    T getUpdate();
}
