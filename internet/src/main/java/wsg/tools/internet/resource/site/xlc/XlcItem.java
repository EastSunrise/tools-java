package wsg.tools.internet.resource.site.xlc;

import wsg.tools.internet.resource.item.IdentifiedItem;
import wsg.tools.internet.resource.item.VideoType;
import wsg.tools.internet.resource.item.intf.StateSupplier;
import wsg.tools.internet.resource.item.intf.TypeSupplier;
import wsg.tools.internet.resource.item.intf.UpdateTimeSupplier;
import wsg.tools.internet.resource.item.intf.YearSupplier;

import javax.annotation.Nonnull;
import java.time.LocalDate;

/**
 * Items of {@link XlcSite}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class XlcItem extends IdentifiedItem implements TypeSupplier, YearSupplier, UpdateTimeSupplier<LocalDate>, StateSupplier {

    private final LocalDate updateDate;
    private final VideoType type;
    private final String state;
    private Integer year;

    XlcItem(int id, @Nonnull String url, LocalDate updateDate, VideoType type, String state) {
        super(id, url);
        this.updateDate = updateDate;
        this.type = type;
        this.state = state;
    }

    @Override
    public VideoType getType() {
        return type;
    }

    @Override
    public Integer getYear() {
        return year;
    }

    void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public LocalDate getUpdateTime() {
        return updateDate;
    }

    @Override
    public String getState() {
        return state;
    }
}
