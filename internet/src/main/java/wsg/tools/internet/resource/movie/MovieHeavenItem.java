package wsg.tools.internet.resource.movie;

import java.time.LocalDate;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.resource.common.StateSupplier;
import wsg.tools.internet.resource.common.YearSupplier;

/**
 * Items of {@link MovieHeavenSite}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class MovieHeavenItem extends IdentifiedItem<MovieHeavenType>
    implements YearSupplier, UpdateDateSupplier, StateSupplier {

    private final LocalDate addDate;
    private String state;
    private Integer year;

    MovieHeavenItem(int id, @Nonnull String url, MovieHeavenType type, LocalDate addDate) {
        super(id, url, type);
        this.addDate = addDate;
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
