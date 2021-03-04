package wsg.tools.internet.resource.movie;

import wsg.tools.internet.resource.common.*;

import javax.annotation.Nonnull;
import java.time.LocalDate;

/**
 * Items of {@link MovieHeavenSite}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class MovieHeavenItem extends IdentifiedItem implements VideoTypeSupplier, YearSupplier, UpdateDateSupplier, StateSupplier {

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
    public String getState() {
        return state;
    }

    void setState(String state) {
        this.state = state;
    }

    @Override
    public LocalDate lastUpdate() {
        return addDate;
    }
}
