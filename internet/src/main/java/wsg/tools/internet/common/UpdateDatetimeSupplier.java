package wsg.tools.internet.common;

import java.time.LocalDateTime;

/**
 * Supplies the update datetime of the record.
 * <p>
 * Conflict with {@link UpdateDateSupplier}.
 *
 * @author Kingen
 * @since 2021/2/25
 */
@FunctionalInterface
public interface UpdateDatetimeSupplier {

    /**
     * Obtains update datetime.
     *
     * @return update datetime
     */
    LocalDateTime lastUpdate();
}
