package wsg.tools.internet.resource.site;

import wsg.tools.internet.resource.item.IdentifiedItem;
import wsg.tools.internet.resource.item.VideoType;
import wsg.tools.internet.resource.item.intf.TypeSupplier;
import wsg.tools.internet.resource.item.intf.UpdateDateSupplier;
import wsg.tools.internet.resource.item.intf.YearSupplier;
import wsg.tools.internet.video.site.douban.DoubanIdentifier;

import javax.annotation.Nonnull;
import java.time.LocalDate;

/**
 * Items of {@link Y80sSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
public class Y80sItem extends IdentifiedItem implements YearSupplier, TypeSupplier, DoubanIdentifier, UpdateDateSupplier {

    private final LocalDate updateDate;
    private final VideoType type;
    private Integer year;
    private Long dbId;

    Y80sItem(int id, @Nonnull String url, LocalDate updateDate, VideoType type) {
        super(id, url);
        this.updateDate = updateDate;
        this.type = type;
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
