package wsg.tools.internet.resource.movie;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.resource.common.CoverSupplier;

/**
 * News items of Grape site, resources of which are all downloadable.All of the items belong to
 * {@link GrapeVodType#BT_MOVIE}.
 *
 * @author Kingen
 * @since 2021/2/4
 */
public class GrapeNewsItem extends IdentifiedItem<GrapeVodType>
    implements UpdateDateSupplier, CoverSupplier {

    private final LocalDate releaseDate;
    private URL cover;

    GrapeNewsItem(int id, @Nonnull String url, LocalDate releaseDate) {
        super(id, url, GrapeVodType.BT_MOVIE);
        this.releaseDate = Objects.requireNonNull(releaseDate, "the release date of an item");
    }

    @Override
    public URL getCover() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = Objects.requireNonNull(cover, "the cover of an item");
    }

    @Override
    public LocalDate lastUpdate() {
        return releaseDate;
    }
}
