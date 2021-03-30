package wsg.tools.internet.base;

import java.time.LocalDate;

/**
 * Supplies the update date of the record.
 * <p>
 * Note that this interface is conflict with {@link UpdateDatetimeSupplier}.
 *
 * @author Kingen
 * @since 2021/2/25
 */
@EntityInterface
public interface UpdateDateSupplier {

    /**
     * Obtains update date.
     *
     * @return update date
     */
    LocalDate lastUpdate();
}
