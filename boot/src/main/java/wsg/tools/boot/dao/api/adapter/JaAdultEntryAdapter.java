package wsg.tools.boot.dao.api.adapter;

import java.time.LocalDate;
import wsg.tools.internet.info.adult.view.ActressSupplier;
import wsg.tools.internet.info.adult.view.SerialNumSupplier;

/**
 * Adapter that supplies properties to build a {@link wsg.tools.boot.pojo.entity.adult.JaAdultVideoEntity}.
 *
 * @author Kingen
 * @since 2021/4/29
 */
public interface JaAdultEntryAdapter extends SerialNumSupplier, AdultEntryAdapter, ActressSupplier {

    /**
     * Returns whether the video has a mosaic.
     *
     * @return {@code true} if the video has a mosaic, {@code false} if not, or {@code null} if
     * unknown
     */
    Boolean getMosaic();

    /**
     * Returns the publish date of the video.
     *
     * @return the publish date
     */
    LocalDate getPublish();

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
