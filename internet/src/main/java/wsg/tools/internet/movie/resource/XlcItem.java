package wsg.tools.internet.movie.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.support.InvalidResourceException;
import wsg.tools.internet.movie.common.ResourceState;
import wsg.tools.internet.movie.common.StateSupplier;
import wsg.tools.internet.movie.common.YearSupplier;

/**
 * An item in {@link XlcSite}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class XlcItem extends BaseIdentifiedItem<XlcType>
    implements YearSupplier, UpdateDateSupplier, StateSupplier {

    private final LocalDate updateDate;
    private final ResourceState state;
    private Integer year;

    XlcItem(int id, XlcType subtype, String title, List<Link> links,
        List<InvalidResourceException> exceptions, LocalDate updateDate, ResourceState state) {
        super(id, subtype, title, links, exceptions);
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
    public LocalDate getUpdate() {
        return updateDate;
    }
}
