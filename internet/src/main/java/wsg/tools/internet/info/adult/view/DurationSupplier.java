package wsg.tools.internet.info.adult.view;

import java.time.Duration;
import wsg.tools.internet.base.EntityProperty;

/**
 * Represents a supplier of the duration of the entry.
 *
 * @author Kingen
 * @since 2021/4/13
 */
@EntityProperty
public interface DurationSupplier {

    /**
     * Returns the duration of the entry.
     *
     * @return the duration
     */
    Duration getDuration();
}
