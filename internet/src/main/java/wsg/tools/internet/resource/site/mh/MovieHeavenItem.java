package wsg.tools.internet.resource.site.mh;

import wsg.tools.internet.resource.item.IdentifiedItem;
import wsg.tools.internet.resource.item.VideoType;
import wsg.tools.internet.resource.item.intf.StateSupplier;
import wsg.tools.internet.resource.item.intf.TypeSupplier;
import wsg.tools.internet.resource.item.intf.UpdateTimeSupplier;
import wsg.tools.internet.resource.item.intf.YearSupplier;

import javax.annotation.Nonnull;
import java.time.LocalDate;

/**
 * Items of {@link MovieHeavenSite}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class MovieHeavenItem extends IdentifiedItem implements TypeSupplier, YearSupplier, UpdateTimeSupplier<LocalDate>, StateSupplier {

    private final VideoType type;
    private final LocalDate addDate;
    private String state;
    private Integer year;

    MovieHeavenItem(int id, @Nonnull String url, VideoType type, LocalDate addDate) {
        super(id, url);
        this.type = type;
        this.addDate = addDate;
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
        return addDate;
    }

    @Override
    public String getState() {
        return state;
    }

    void setState(String state) {
        this.state = state;
    }
}
