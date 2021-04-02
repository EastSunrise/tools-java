package wsg.tools.internet.info.adult;

import javax.annotation.Nonnull;
import wsg.tools.internet.base.EntityInterface;

/**
 * Supplies an amateur adult entry.
 *
 * @author Kingen
 * @since 2021/3/28
 */
@EntityInterface
public interface AmateurSupplier {

    /**
     * Returns an amateur adult entry.
     *
     * @return an amateur adult entry, not null
     */
    @Nonnull
    AmateurAdultEntry getAmateurEntry();
}
