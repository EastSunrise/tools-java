package wsg.tools.internet.info.adult.view;

import java.time.Duration;

/**
 * Represents a supplier of the duration of the video.
 *
 * @author Kingen
 * @since 2021/4/13
 */
public interface DurationSupplier {

    /**
     * Returns the duration of the video.
     *
     * @return the duration
     */
    Duration getDuration();
}
