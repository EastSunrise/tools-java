package wsg.tools.boot.dao.api.impl;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.info.adult.ggg.GggGood;
import wsg.tools.internet.info.adult.ggg.GggGoodView;
import wsg.tools.internet.info.adult.view.AlbumSupplier;
import wsg.tools.internet.info.adult.view.Describable;
import wsg.tools.internet.info.adult.view.FormalJaAdultEntry;
import wsg.tools.internet.info.adult.view.Tagged;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;

/**
 * Adapts a {@link GggGood} and a {@link GggGoodView} to an adult entry.
 *
 * @author Kingen
 * @since 2021/4/15
 */
public class GggGoodAdapter implements IntIdentifier, FormalJaAdultEntry, TitledAdultEntry,
    UpdateDateSupplier, Tagged, Describable, CoverSupplier, AlbumSupplier {

    private final GggGood good;
    private final GggGoodView view;

    public GggGoodAdapter(@Nonnull GggGood good, @Nonnull GggGoodView view) {
        AssertUtils.requireEquals(good.getId(), view.getId());
        this.good = good;
        this.view = view;
    }

    @Override
    public int getId() {
        return good.getId();
    }

    @Override
    public String getTitle() {
        return good.getTitle();
    }

    @Override
    public URL getCoverURL() {
        return view.getCoverURL();
    }

    @Override
    public LocalDate getUpdate() {
        return good.getUpdate();
    }

    @Nonnull
    @Override
    public List<URL> getAlbum() {
        List<URL> urls = new ArrayList<>(2);
        CollectionUtils.addIgnoreNull(urls, good.getImageURL());
        CollectionUtils.addIgnoreNull(urls, view.getImageURL());
        return urls;
    }

    @Override
    public Boolean getMosaic() {
        return good.getMosaic();
    }

    @Override
    public LocalDate getRelease() {
        return good.getRelease();
    }

    @Override
    public String getProducer() {
        return good.getProducer();
    }

    @Override
    public String getDistributor() {
        return good.getDistributor();
    }

    @Override
    public String getSeries() {
        return good.getSeries();
    }

    @Override
    public String getSerialNum() {
        return good.getSerialNum();
    }

    @Nonnull
    @Override
    public List<String> getActresses() {
        return good.getActresses();
    }

    @Override
    public String getDescription() {
        return good.getDescription();
    }

    @Nonnull
    @Override
    public String[] getTags() {
        return good.getTags();
    }
}
