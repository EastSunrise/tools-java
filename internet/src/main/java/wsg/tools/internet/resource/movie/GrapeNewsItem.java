package wsg.tools.internet.resource.movie;

import wsg.tools.internet.resource.common.UpdateDateSupplier;
import wsg.tools.internet.resource.common.VideoType;
import wsg.tools.internet.resource.common.VideoTypeSupplier;
import wsg.tools.internet.resource.common.YearSupplier;

import javax.annotation.Nonnull;
import java.time.LocalDate;

/**
 * News items of Grape site, resources of which are all downloadable.
 *
 * @author Kingen
 * @since 2021/2/4
 */
public class GrapeNewsItem extends IdentifiedItem implements VideoTypeSupplier, YearSupplier, UpdateDateSupplier {

    private final LocalDate releaseDate;
    private Integer year;

    GrapeNewsItem(int id, @Nonnull String url, LocalDate releaseDate) {
        super(id, url);
        this.releaseDate = releaseDate;
    }

    @Override
    public VideoType getType() {
        return VideoType.MOVIE;
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
