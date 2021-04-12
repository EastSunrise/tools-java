package wsg.tools.internet.common;

import java.time.LocalDateTime;
import wsg.tools.internet.base.EntityInterface;

/**
 * Represents a supplier of the update datetime of the entity.
 * <p>
 * Note that this interface is conflict with {@link UpdateDateSupplier}.
 *
 * @author Kingen
 * @since 2021/2/25
 */
@EntityInterface
public interface UpdateDatetimeSupplier extends UpdateTemporalSupplier<LocalDateTime> {

    /**
     * Returns the update datetime of the entity.
     *
     * @return the update datetime
     */
    @Override
    LocalDateTime getUpdate();
}
