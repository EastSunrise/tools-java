package wsg.tools.internet.resource.item.intf;

import java.time.LocalDateTime;

/**
 * Supply update datetime of the item.
 * <p>
 * Conflict with {@link UpdateDateSupplier}.
 *
 * @author Kingen
 * @since 2021/2/25
 */
public interface UpdateDatetimeSupplier {

    /**
     * Obtains update datetime.
     *
     * @return update datetime
     */
    LocalDateTime lastUpdate();
}
