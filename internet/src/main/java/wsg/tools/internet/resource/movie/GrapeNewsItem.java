package wsg.tools.internet.resource.movie;

import java.time.LocalDate;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.resource.common.YearSupplier;

/**
 * News items of Grape site, resources of which are all downloadable.All of the items belong to
 * {@link GrapeVodType#BT_MOVIE}.
 *
 * @author Kingen
 * @since 2021/2/4
 */
public class GrapeNewsItem extends IdentifiedItem<GrapeVodType>
    implements YearSupplier, UpdateDateSupplier {

    private final LocalDate releaseDate;
    private Integer year;

    GrapeNewsItem(int id, @Nonnull String url, LocalDate releaseDate) {
        super(id, url, GrapeVodType.BT_MOVIE);
        this.releaseDate = releaseDate;
    }

    @Override
    public Integer getYear() {
        return year;
    }

    void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public LocalDate lastUpdate() {
        return releaseDate;
    }
}
