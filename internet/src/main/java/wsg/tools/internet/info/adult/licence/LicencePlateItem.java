package wsg.tools.internet.info.adult.licence;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.support.BasicSiblingEntity;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.view.AmateurJaAdultEntry;
import wsg.tools.internet.info.adult.view.Describable;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.ImageSupplier;
import wsg.tools.internet.info.adult.view.Tagged;

/**
 * An item in the {@link LicencePlateSite}.
 *
 * @author Kingen
 * @see LicencePlateSite#findById(String)
 * @since 2021/3/9
 */
public class LicencePlateItem extends BasicSiblingEntity<String>
    implements IntIdentifier, PathSupplier, UpdateDatetimeSupplier, AmateurJaAdultEntry,
    DurationSupplier, ImageSupplier, Tagged, Describable {

    private final int id;
    private final String serialNum;
    private final String description;
    private final LocalDateTime updateTime;

    private URL image;
    private String performer;
    private Duration duration;
    private LocalDate release;
    private String producer;
    private String distributor;
    private String series;
    private String[] tags = new String[0];

    LicencePlateItem(int id, String serialNum, String description, LocalDateTime updateTime,
        String previous, String next) {
        super(previous, next);
        this.id = id;
        this.serialNum = serialNum;
        this.description = description;
        this.updateTime = updateTime;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getAsPath() {
        return serialNum.toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerialNum() {
        return serialNum;
    }

    @Override
    public URL getImageURL() {
        return image;
    }

    void setImage(URL image) {
        this.image = image;
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

    @Nonnull
    @Override
    public String[] getTags() {
        return tags;
    }

    void setTags(String[] tags) {
        this.tags = tags;
    }
}
