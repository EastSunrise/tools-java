package wsg.tools.internet.movie.resource;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.UpdateDateSupplier;
import wsg.tools.internet.movie.common.ResourceState;
import wsg.tools.internet.movie.common.StateSupplier;
import wsg.tools.internet.movie.common.YearSupplier;

/**
 * Items of {@link MovieHeavenSite}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class MovieHeavenItem extends BaseIdentifiedItem
    implements YearSupplier, UpdateDateSupplier, StateSupplier {

    private final MovieHeavenType type;
    private final LocalDate addDate;
    private final URL cover;
    private ResourceState state;
    private Integer year;

    MovieHeavenItem(int id, @Nonnull String url, MovieHeavenType type, LocalDate addDate,
        URL cover) {
        super(id, url);
        this.type = Objects.requireNonNull(type);
        this.addDate = Objects.requireNonNull(addDate, "the added date of an item");
        this.cover = Objects.requireNonNull(cover, "the cover of an item");
    }

    @Override
    public Integer getYear() {
        return year;
    }

    void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public ResourceState getState() {
        return state;
    }

    void setState(ResourceState state) {
        this.state = state;
    }

    @Override
    public LocalDate lastUpdate() {
        return addDate;
    }

    @Override
    public URL getCover() {
        return cover;
    }

    @Override
    public int getSubtype() {
        return type.getId();
    }
}
