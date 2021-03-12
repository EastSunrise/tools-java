package wsg.tools.internet.resource.movie;

import java.time.LocalDate;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.resource.common.YearSupplier;

/**
 * An item in {@link Y80sSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
public class Y80sItem extends IdentifiedItem<Y80sType>
    implements YearSupplier, DoubanIdentifier, UpdateDateSupplier {

    private final LocalDate updateDate;
    private Integer year;
    private Long dbId;

    Y80sItem(int id, @Nonnull String url, LocalDate updateDate, Y80sType type) {
        super(id, url, type);
        this.updateDate = updateDate;
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
}
