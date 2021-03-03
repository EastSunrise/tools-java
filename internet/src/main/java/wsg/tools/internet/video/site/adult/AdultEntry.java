package wsg.tools.internet.video.site.adult;

import lombok.Getter;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.video.site.adult.mr.enums.Mosaic;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * The entry of an adult video.
 *
 * @author Kingen
 * @since 2021/3/3
 */
@Getter
public class AdultEntry extends BaseAdultEntry {

    private final String cover;
    private String title;
    private Mosaic mosaic;
    private Duration duration;
    private LocalDate release;
    private String director;
    private String producer;
    private String distributor;
    private String series;
    private List<String> tags;

    protected AdultEntry(String code, String cover) {
        super(code);
        this.cover = cover;
    }

    protected AdultEntry(String code, String cover, String title, Mosaic mosaic, Duration duration, LocalDate release,
                         String director, String producer, String distributor, String series, List<String> tags) {
        super(code);
        this.cover = AssertUtils.requireNotBlank(cover);
        this.title = title;
        this.mosaic = mosaic;
        this.duration = duration;
        this.release = release;
        this.director = director;
        this.producer = producer;
        this.distributor = distributor;
        this.series = series;
        this.tags = tags;
    }
}
