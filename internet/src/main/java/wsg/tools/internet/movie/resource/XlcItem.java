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
 * An item in {@link XlcSite}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class XlcItem extends BaseIdentifiedItem
    implements YearSupplier, UpdateDateSupplier, StateSupplier {

    private final XlcType type;
    private final LocalDate updateDate;
    private final ResourceState state;
    private URL cover;
    private Integer year;

    XlcItem(int id, @Nonnull String url, LocalDate updateDate, XlcType type, ResourceState state) {
        super(id, url);
        this.type = Objects.requireNonNull(type);
        this.updateDate = Objects.requireNonNull(updateDate, "the update date of an item");
        this.state = state;
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

    @Override
    public LocalDate lastUpdate() {
        return updateDate;
    }

    @Override
    public URL getCover() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = Objects.requireNonNull(cover, "the cover of an item");
    }

    @Override
    public int getSubtype() {
        return type.getId();
    }
}
