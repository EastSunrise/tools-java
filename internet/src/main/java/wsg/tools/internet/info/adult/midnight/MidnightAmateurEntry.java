package wsg.tools.internet.info.adult.midnight;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import wsg.tools.internet.info.adult.view.AmateurJaAdultEntry;
import wsg.tools.internet.info.adult.view.DurationSupplier;

/**
 * An item with an amateur adult entry.
 *
 * @author Kingen
 * @see MidnightSite#findAmateurEntry(MidnightColumn, int)
 * @since 2021/3/28
 */
public class MidnightAmateurEntry extends MidnightAlbum
    implements AmateurJaAdultEntry, DurationSupplier {

    private String serialNum;
    private String performer;
    private Duration duration;
    private LocalDate release;
    private String producer;
    private String distributor;
    private String series;

    MidnightAmateurEntry(int id, String title, LocalDateTime addTime, List<URL> album) {
        super(id, title, addTime, album);
    }

    @Override
    public String getSerialNum() {
        return serialNum;
    }

    void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    @Override
    public String getPerformer() {
        return performer;
    }

    void setPerformer(String performer) {
        this.performer = performer;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public Boolean getMosaic() {
        return true;
    }

    @Override
    public LocalDate getRelease() {
        return release;
    }

    void setRelease(LocalDate release) {
        this.release = release;
    }

    @Override
    public String getProducer() {
        return producer;
    }

    void setProducer(String producer) {
        this.producer = producer;
    }

    @Override
    public String getDistributor() {
        return distributor;
    }

    void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    @Override
    public String getSeries() {
        return series;
    }

    void setSeries(String series) {
        this.series = series;
    }
}
