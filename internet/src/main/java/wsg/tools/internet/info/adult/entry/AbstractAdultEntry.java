package wsg.tools.internet.info.adult.entry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import wsg.tools.internet.info.adult.common.Mosaic;

/**
 * The entry of an adult video.
 *
 * @author Kingen
 * @since 2021/3/3
 */
public abstract class AbstractAdultEntry extends BasicAdultEntry {

    private Mosaic mosaic;
    private Duration duration;
    private LocalDate release;
    private String director;
    private String producer;
    private String distributor;
    private String series;
    private List<String> tags;

    AbstractAdultEntry(String code) {
        super(code);
    }

    public Mosaic getMosaic() {
        return mosaic;
    }

    void setMosaic(Mosaic mosaic) {
        this.mosaic = mosaic;
    }

    public Duration getDuration() {
        return duration;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDate getRelease() {
        return release;
    }

    void setRelease(LocalDate release) {
        this.release = release;
    }

    public String getDirector() {
        return director;
    }

    void setDirector(String director) {
        this.director = director;
    }

    public String getProducer() {
        return producer;
    }

    void setProducer(String producer) {
        this.producer = producer;
    }

    public String getDistributor() {
        return distributor;
    }

    void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public String getSeries() {
        return series;
    }

    void setSeries(String series) {
        this.series = series;
    }

    public List<String> getTags() {
        return tags;
    }

    void setTags(List<String> tags) {
        this.tags = tags;
    }
}
