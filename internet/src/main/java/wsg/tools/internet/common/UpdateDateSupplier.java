package wsg.tools.internet.common;

import java.time.LocalDate;
import wsg.tools.internet.base.EntityInterface;

/**
 * Represents a supplier of the update date of the entity.
 * <p>
 * Note that this interface is conflict with {@link UpdateDatetimeSupplier}.
 *
 * @author Kingen
 * @since 2021/2/25
 */
@EntityInterface
public interface UpdateDateSupplier extends UpdateTemporalSupplier<LocalDate> {

    /**
     * Returns the update date of the entity.
     *
     * @return the update date
     */
    @Override
    LocalDate getUpdate();
}
