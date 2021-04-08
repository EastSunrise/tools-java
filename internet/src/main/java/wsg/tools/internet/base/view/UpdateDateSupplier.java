package wsg.tools.internet.base.view;

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
public interface UpdateDateSupplier {

    /**
     * Returns the update date of the entity.
     *
     * @return the update date
     */
    LocalDate getUpdate();
}
