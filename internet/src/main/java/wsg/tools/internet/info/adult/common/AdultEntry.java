package wsg.tools.internet.info.adult.common;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import wsg.tools.common.lang.AssertUtils;

/**
 * The entry of an adult video.
 *
 * @author Kingen
 * @since 2021/3/3
 */
@Getter
public class AdultEntry {

    private final String code;
    private String title;
    private String description;
    private Mosaic mosaic;
    private Duration duration;
    private LocalDate release;
    private String director;
    private String producer;
    private String distributor;
    private String series;
    private List<String> tags;
    private List<String> images;

    AdultEntry(String code) {
        this.code = AssertUtils.requireNotBlank(code, "code of an adult entry");
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setMosaic(Mosaic mosaic) {
        this.mosaic = mosaic;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    void setRelease(LocalDate release) {
        this.release = release;
    }

    void setDirector(String director) {
        this.director = director;
    }

    void setProducer(String producer) {
        this.producer = producer;
    }

    void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    void setSeries(String series) {
        this.series = series;
    }

    void setTags(List<String> tags) {
        if (CollectionUtils.isNotEmpty(tags)) {
            this.tags = Collections.unmodifiableList(tags);
        }
    }

    void setImages(List<String> images) {
        if (CollectionUtils.isNotEmpty(images)) {
            this.images = Collections.unmodifiableList(images);
        }
    }
}
