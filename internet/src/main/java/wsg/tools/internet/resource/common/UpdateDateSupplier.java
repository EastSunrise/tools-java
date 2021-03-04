package wsg.tools.internet.resource.common;

import java.time.LocalDate;

/**
 * Supply update date of the item.
 * <p>
 * Conflict with {@link UpdateDatetimeSupplier}.
 *
 * @author Kingen
 * @since 2021/2/25
 */
public interface UpdateDateSupplier {

    /**
     * Obtains update date.
     *
     * @return update date
     */
    LocalDate lastUpdate();
}
