package wsg.tools.internet.movie.resource;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.UpdateDateSupplier;
import wsg.tools.internet.movie.common.YearSupplier;
import wsg.tools.internet.movie.douban.DoubanIdentifier;

/**
 * An item in {@link Y80sSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
public class Y80sItem extends BaseIdentifiedItem
    implements YearSupplier, DoubanIdentifier, UpdateDateSupplier {

    private final Y80sType type;
    private final LocalDate updateDate;
    private URL cover;
    private Integer year;
    private Long dbId;

    Y80sItem(int id, @Nonnull String url, Y80sType type, LocalDate updateDate) {
        super(id, url);
        this.type = Objects.requireNonNull(type);
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

    @Override
    public int getSubtype() {
        return type.getId();
    }
}
