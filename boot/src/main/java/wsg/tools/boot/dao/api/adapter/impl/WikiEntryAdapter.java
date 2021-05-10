package wsg.tools.boot.dao.api.adapter.impl;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import wsg.tools.boot.dao.api.adapter.JaAdultEntryAdapter;
import wsg.tools.internet.info.adult.wiki.WikiAdultEntry;

/**
 * Adapter that accepts a {@link WikiAdultEntry} and implements {@link JaAdultEntryAdapter}.
 *
 * @author Kingen
 * @since 2021/4/29
 */
public class WikiEntryAdapter implements JaAdultEntryAdapter {

    private final WikiAdultEntry entry;

    public WikiEntryAdapter(@Nonnull WikiAdultEntry entry) {
        this.entry = entry;
    }

    @Override
    public Boolean getMosaic() {
        return entry.getMosaic();
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
        return entry.getTitle();
    }

    @Override
    public URL getCoverURL() {
        return entry.getCoverURL();
    }

    @Nonnull
    @Override
    public List<String> getActresses() {
        return entry.getActresses();
    }

    @Override
    public Duration getDuration() {
        return entry.getDuration();
    }

    @Nonnull
    @Override
    public List<URL> getImages() {
        return new ArrayList<>();
    }

    @Override
    public String getSerialNum() {
        return entry.getSerialNum();
    }

    @Nonnull
    @Override
    public Set<String> getTags() {
        return entry.getTags();
    }
}
