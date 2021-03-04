package wsg.tools.internet.info.adult.mr;

import lombok.Getter;
import wsg.tools.internet.info.adult.BasicAdultEntry;
import wsg.tools.internet.info.adult.common.Mosaic;

import java.time.Duration;
import java.time.LocalDate;

/**
 * An adult entry with simple information.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Getter
public class SimpleAdultEntry extends BasicAdultEntry {

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
