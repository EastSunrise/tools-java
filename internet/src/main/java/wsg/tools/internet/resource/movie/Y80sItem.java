package wsg.tools.internet.resource.movie;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.resource.common.CoverSupplier;
import wsg.tools.internet.resource.common.YearSupplier;

/**
 * An item in {@link Y80sSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
public class Y80sItem extends IdentifiedItem<Y80sType>
    implements YearSupplier, DoubanIdentifier, UpdateDateSupplier, CoverSupplier {

    private final LocalDate updateDate;
    private URL cover;
    private Integer year;
    private Long dbId;

    Y80sItem(int id, @Nonnull String url, LocalDate updateDate, Y80sType type) {
        super(id, url, type);
        this.updateDate = Objects.requireNonNull(updateDate, "the update date of an item");
    }

    @Override
    public Integer getYear() {
        return year;
    }

    void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public Long getDbId() {
        return dbId;
    }

    void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    @Override
    public LocalDate lastUpdate() {
        return updateDate;
    }

    @Override
    public URL getCover() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = Objects.requireNonNull(cover, "the cover of an item");
    }
}
