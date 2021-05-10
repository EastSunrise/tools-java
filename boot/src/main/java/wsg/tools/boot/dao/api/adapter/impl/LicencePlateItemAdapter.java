package wsg.tools.boot.dao.api.adapter.impl;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import wsg.tools.boot.dao.api.adapter.JaAdultEntryAdapter;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.licence.LicencePlateItem;

/**
 * Adapter that accepts a {@link LicencePlateItem} and implements {@link JaAdultEntryAdapter}.
 *
 * @author Kingen
 * @since 2021/4/29
 */
public class LicencePlateItemAdapter implements IntIdentifier,
    JaAdultEntryAdapter, UpdateDatetimeSupplier {

    private final LicencePlateItem item;

    public LicencePlateItemAdapter(@Nonnull LicencePlateItem item) {
        this.item = item;
    }

    @Override
    public int getId() {
        return item.getId();
    }

    @Override
    public LocalDateTime getUpdate() {
        return item.getUpdate();
    }

    @Override
    public Boolean getMosaic() {
        return true;
    }

    @Override
    public LocalDate getPublish() {
        return item.getPublish();
    }

    @Override
    public String getProducer() {
        return item.getProducer();
    }

    @Override
    public String getDistributor() {
        return item.getDistributor();
    }

    @Override
    public String getSeries() {
        return item.getSeries();
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public URL getCoverURL() {
        return null;
    }

    @Nonnull
    @Override
    public List<String> getActresses() {
        return new ArrayList<>();
    }

    @Override
    public Duration getDuration() {
        return item.getDuration();
    }

    @Nonnull
    @Override
    public List<URL> getImages() {
        return List.of(item.getImageURL());
    }

    @Override
    public String getSerialNum() {
        return item.getSerialNum();
    }

    @Nonnull
    @Override
    public Set<String> getTags() {
        return item.getTags();
    }
}
