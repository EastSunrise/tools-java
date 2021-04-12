package wsg.tools.internet.movie.resource;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.UpdateDateSupplier;

/**
 * News items of Grape site, resources of which are all downloadable.All of the items belong to
 * {@link GrapeVodType#BT_MOVIE}.
 *
 * @author Kingen
 * @since 2021/2/4
 */
public class GrapeNewsItem extends BaseIdentifiedItem<GrapeVodType> implements UpdateDateSupplier {

    private final LocalDate releaseDate;
    private URL cover;

    GrapeNewsItem(@Nonnull URL source, int id, String title, LocalDate releaseDate) {
        super(GrapeVodType.BT_MOVIE, source, id, title);
        this.releaseDate = Objects.requireNonNull(releaseDate, "the release date of an item");
    }

    @Override
    public URL getCoverURL() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = Objects.requireNonNull(cover, "the cover of an item");
    }

    @Override
    public LocalDate getUpdate() {
        return releaseDate;
    }
}
