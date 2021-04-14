package wsg.tools.internet.info.adult.licence;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import wsg.tools.internet.base.view.Identifier;
import wsg.tools.internet.base.view.NextSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.view.AmateurJaAdultEntry;
import wsg.tools.internet.info.adult.view.Describable;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.ImageSupplier;

/**
 * An item on the {@link AmateurCatSite}.
 *
 * @author Kingen
 * @see AmateurCatSite#findById(String)
 * @since 2021/2/28
 */
public class AmateurCatItem implements Identifier<String>, AmateurJaAdultEntry, DurationSupplier,
    ImageSupplier, Describable, UpdateDatetimeSupplier, NextSupplier<String> {

    private final String id;
    private final String serialNum;
    private final URL image;
    private final String description;
    private final String author;
    private final LocalDateTime updated;
    private final LocalDateTime published;
    private final String next;

    private String performer;
    private Duration duration;
    private LocalDate release;
    private String producer;
    private String distributor;
    private String series;
    private String[] tags;

    AmateurCatItem(String id, String serialNum, URL image, String description,
        String author, LocalDateTime updated, LocalDateTime published, String next) {
        this.id = id;
        this.serialNum = serialNum;
        this.image = image;
        this.description = description;
        this.author = author;
        this.updated = updated;
        this.published = published;
        this.next = next;
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
    public URL getImageURL() {
        return image;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getPublished() {
        return published;
    }

    @Override
    public LocalDateTime getUpdate() {
        return updated;
    }

    @Override
    public String getNextId() {
        return next;
    }

    @Override
    public String getPerformer() {
        return performer;
    }

    void setPerformer(String performer) {
        this.performer = performer;
    }

    @Override
    public Boolean getMosaic() {
        return true;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
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

    public String[] getTags() {
        return tags;
    }

    void setTags(String[] tags) {
        this.tags = tags;
    }
}
