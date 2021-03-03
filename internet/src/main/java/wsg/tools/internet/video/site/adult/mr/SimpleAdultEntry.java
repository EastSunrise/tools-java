package wsg.tools.internet.video.site.adult.mr;

import lombok.Getter;
import wsg.tools.internet.video.site.adult.BaseAdultEntry;
import wsg.tools.internet.video.site.adult.mr.enums.Mosaic;

import java.time.Duration;
import java.time.LocalDate;

/**
 * An adult entry with simple information.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Getter
public class SimpleAdultEntry extends BaseAdultEntry {

    /**
     * May be blocked to null
     */
    private String title;
    private Mosaic mosaic;
    private Duration duration;
    private LocalDate release;
    private String distributor;

    SimpleAdultEntry(String code) {
        super(code);
    }

    void setTitle(String title) {
        this.title = title;
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

    void setDistributor(String distributor) {
        this.distributor = distributor;
    }
}
