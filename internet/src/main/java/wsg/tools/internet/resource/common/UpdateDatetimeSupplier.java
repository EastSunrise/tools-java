package wsg.tools.internet.resource.common;

import java.time.LocalDateTime;

/**
 * Supply update datetime of the item.
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