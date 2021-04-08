package wsg.tools.internet.info.adult.licence;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import wsg.tools.internet.base.view.Identifier;
import wsg.tools.internet.base.view.NextSupplier;
import wsg.tools.internet.base.view.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.view.AmateurJaAdultEntry;
import wsg.tools.internet.info.adult.view.Describable;
import wsg.tools.internet.info.adult.view.Tagged;

/**
 * An item in the {@link LicencePlateSite}.
 *
 * @author Kingen
 * @see LicencePlateSite#findById(String)
 * @since 2021/3/9
 */
public class LicencePlateItem
    implements Identifier<String>, UpdateDatetimeSupplier, NextSupplier<String>,
    AmateurJaAdultEntry, Tagged, Describable {

    private final String id;
    private final String serialNum;
    private final String description;
    private final LocalDateTime updateTime;
    private final String nextId;

    private URL cover;
    private String performer;
    private Duration duration;
    private LocalDate release;
    private String producer;
    private String distributor;
    private String series;
    private String[] tags;

    LicencePlateItem(String id, String serialNum, String description, LocalDateTime updateTime,
        String nextId) {
        this.id = id;
        this.serialNum = serialNum;
        this.description = description;
        this.updateTime = updateTime;
        this.nextId = nextId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getSerialNum() {
        return serialNum;
    }

    @Override
    public URL getCover() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = cover;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public LocalDateTime getUpdate() {
        return updateTime;
    }

    @Override
    public String nextId() {
        return nextId;
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

    @Override
    public String[] getTags() {
        return tags;
    }

    void setTags(String[] tags) {
        this.tags = tags;
    }
}
