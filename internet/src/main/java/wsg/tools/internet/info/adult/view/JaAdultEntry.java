package wsg.tools.internet.info.adult.view;

import java.time.LocalDate;

/**
 * An entry of a Japanese adult video.
 *
 * @author Kingen
 * @since 2021/4/7
 */
public interface JaAdultEntry extends SerialNumSupplier, AdultEntry {

    /**
     * Returns whether the video has a mosaic.
     *
     * @return {@code true} if the video has a mosaic, otherwise {@code false}
     */
    Boolean getMosaic();

    /**
     * Returns the release date of the video.
     *
     * @return the release date
     */
    LocalDate getRelease();

    /**
     * Returns the producer of the video.
     *
     * @return the producer
     */
    String getProducer();

    /**
     * Returns the distributor of the video.
     *
     * @return the distributor
     */
    String getDistributor();

    /**
     * Returns the series of the video.
     *
     * @return the series
     */
    String getSeries();
}
