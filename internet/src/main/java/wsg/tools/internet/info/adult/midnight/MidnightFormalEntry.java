package wsg.tools.internet.info.adult.midnight;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.FormalJaAdultEntry;

/**
 * An item with a formal adult entry.
 *
 * @author Kingen
 * @see MidnightSite#findFormalEntry(int)
 * @since 2021/3/10
 */
public class MidnightFormalEntry extends MidnightAlbum
    implements FormalJaAdultEntry, DurationSupplier {

    private String serialNum;
    private List<String> actresses;
    private Duration duration;
    private LocalDate release;
    private String producer;
    private String distributor;
    private String series;

    MidnightFormalEntry(int id, String title, LocalDateTime addTime, List<URL> album) {
        super(id, title, addTime, album);
    }

    @Override
    public String getSerialNum() {
        return serialNum;
    }

    void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    @Nonnull
    @Override
    public List<String> getActresses() {
        return actresses;
    }

    void setActresses(List<String> actresses) {
        this.actresses = actresses;
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
        return null;
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
