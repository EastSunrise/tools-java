package wsg.tools.internet.resource.movie;

import java.time.LocalDate;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.resource.common.StateSupplier;
import wsg.tools.internet.resource.common.YearSupplier;

/**
 * An item in {@link XlcSite}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class XlcItem extends IdentifiedItem<XlcType>
    implements YearSupplier, UpdateDateSupplier, StateSupplier {

    private final LocalDate updateDate;
    private final String state;
    private Integer year;

    XlcItem(int id, @Nonnull String url, LocalDate updateDate, XlcType type, String state) {
        super(id, url, type);
        this.updateDate = updateDate;
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
    public String getState() {
        return state;
    }

    @Override
    public LocalDate lastUpdate() {
        return updateDate;
    }
}
