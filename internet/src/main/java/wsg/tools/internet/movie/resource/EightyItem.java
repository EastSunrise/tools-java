package wsg.tools.internet.movie.resource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.support.InvalidResourceException;
import wsg.tools.internet.movie.common.YearSupplier;

/**
 * An item on {@link EightyMovieSite}.
 *
 * @author Kingen
 * @since 2021/6/15
 */
public class EightyItem extends BaseIdentifiedItem<EightyType>
    implements YearSupplier, UpdateDateSupplier {

    private final int year;
    private final LocalDate update;
    private List<String> aka = new ArrayList<>(0);

    EightyItem(int id, @Nonnull EightyType subtype, @Nonnull String title, List<Link> links,
        List<InvalidResourceException> exceptions, int year, LocalDate update) {
        super(id, subtype, title, links, exceptions);
        this.year = year;
        this.update = update;
    }

    @Override
    public Integer getYear() {
        return year;
    }

    public List<String> getAka() {
        return aka;
    }

    void setAka(List<String> aka) {
        this.aka = aka;
    }

    @Override
    public LocalDate getUpdate() {
        return update;
    }
}
