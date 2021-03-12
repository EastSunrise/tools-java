package wsg.tools.internet.common;

import java.time.LocalDate;

/**
 * Supplies the update date of the record.
 * <p>
 * Conflict with {@link UpdateDatetimeSupplier}.
 *
 * @author Kingen
 * @since 2021/2/25
 */
@FunctionalInterface
public interface UpdateDateSupplier {

    /**
     * Obtains update date.
     *
     * @return update date
     */
    LocalDate lastUpdate();
}
