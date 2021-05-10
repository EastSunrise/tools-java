package wsg.tools.internet.info.adult.midnight;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.SerialNumSupplier;

/**
 * An adult entry on the site.
 *
 * @author Kingen
 * @since 2021/4/29
 */
public class MidnightEntry extends MidnightAlbum implements SerialNumSupplier, DurationSupplier {

    private String serialNum;
    private Duration duration;
    private LocalDate publish;
    private String producer;
    private String distributor;
    private String series;

    MidnightEntry(int id, String title, LocalDateTime addTime, List<URL> images) {
        super(id, title, addTime, images);
    }

    @Override
    public String getSerialNum() {
        return serialNum;
    }

    void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDate getPublish() {
        return publish;
    }

    void setPublish(LocalDate publish) {
        this.publish = publish;
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
}
