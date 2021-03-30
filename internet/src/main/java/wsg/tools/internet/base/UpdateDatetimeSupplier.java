package wsg.tools.internet.base;

import java.time.LocalDateTime;

/**
 * Supplies the update datetime of the record.
 * <p>
 * Note that this interface is conflict with {@link UpdateDateSupplier}.
 *
 * @author Kingen
 * @since 2021/2/25
 */
@EntityInterface
public interface UpdateDatetimeSupplier {

    /**
     * Obtains update datetime.
     *
     * @return update datetime
     */
    LocalDateTime lastUpdate();
}
