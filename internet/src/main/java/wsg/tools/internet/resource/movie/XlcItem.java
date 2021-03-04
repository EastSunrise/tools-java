package wsg.tools.internet.resource.movie;

import wsg.tools.internet.resource.common.*;

import javax.annotation.Nonnull;
import java.time.LocalDate;

/**
 * Items of {@link XlcSite}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class XlcItem extends IdentifiedItem implements VideoTypeSupplier, YearSupplier, UpdateDateSupplier, StateSupplier {

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
    public String getState() {
        return state;
    }

    @Override
    public LocalDate lastUpdate() {
        return updateDate;
    }
}
