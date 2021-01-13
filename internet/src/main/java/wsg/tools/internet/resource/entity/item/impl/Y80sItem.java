package wsg.tools.internet.resource.entity.item.impl;

import lombok.Setter;
import wsg.tools.internet.resource.entity.item.base.IdentifiedItem;
import wsg.tools.internet.resource.entity.item.base.TypeSupplier;
import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.entity.item.base.YearSupplier;
import wsg.tools.internet.resource.site.impl.Y80sSite;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Items of {@link Y80sSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
@Setter
public class Y80sItem extends IdentifiedItem implements YearSupplier, TypeSupplier, DoubanIdentifier {

    private Integer year;
    private VideoType type;
    private Long dbId;

    public Y80sItem(int id, @Nonnull String url) {
        super(id, url);
    }

    @Override
    public VideoType getType() {
        return type;
    }

    @Nullable
    @Override
    public Integer getYear() {
        return year;
    }

    @Override
    public Long getDbId() {
        return dbId;
    }
}
