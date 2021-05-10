package wsg.tools.boot.dao.api.adapter.impl;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import wsg.tools.boot.dao.api.adapter.JaAdultEntryAdapter;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.midnight.MidnightEntry;
import wsg.tools.internet.info.adult.view.ActressSupplier;
import wsg.tools.internet.info.adult.view.AmateurSupplier;

/**
 * Adapter that accepts a {@link MidnightEntry} and implements {@link JaAdultEntryAdapter}.
 *
 * @author Kingen
 * @since 2021/4/29
 */
public class MidnightEntryAdapter implements IntIdentifier,
    JaAdultEntryAdapter, UpdateDatetimeSupplier {

    private final MidnightEntry entry;

    public MidnightEntryAdapter(@Nonnull MidnightEntry entry) {
        this.entry = entry;
    }

    @Override
    public int getId() {
        return entry.getId();
    }

    @Override
    public LocalDateTime getUpdate() {
        return entry.getUpdate();
    }

    @Override
    public Boolean getMosaic() {
        return entry instanceof AmateurSupplier ? true : null;
    }

    @Override
    public LocalDate getPublish() {
        return entry.getPublish();
    }

    @Override
    public String getProducer() {
        return entry.getProducer();
    }

    @Override
    public String getDistributor() {
        return entry.getDistributor();
    }

    @Override
    public String getSeries() {
        return entry.getSeries();
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
        if (entry instanceof ActressSupplier) {
            return ((ActressSupplier) entry).getActresses();
        }
        return new ArrayList<>();
    }

    @Override
    public Duration getDuration() {
        return entry.getDuration();
    }

    @Nonnull
    @Override
    public List<URL> getImages() {
        return entry.getImages();
    }

    @Override
    public String getSerialNum() {
        return entry.getSerialNum();
    }

    @Nonnull
    @Override
    public Set<String> getTags() {
        return new HashSet<>(0);
    }
}
