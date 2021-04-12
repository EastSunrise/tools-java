package wsg.tools.internet.info.adult.view;

import java.time.Duration;
import wsg.tools.internet.common.CoverSupplier;

/**
 * Indicates that the entity is an entry of an adult vide with basic information.
 *
 * @author Kingen
 * @since 2021/4/7
 */
public interface AdultEntry extends CoverSupplier {

    /**
     * Returns the duration of the video.
     *
     * @return the duration
     */
    Duration getDuration();
}
