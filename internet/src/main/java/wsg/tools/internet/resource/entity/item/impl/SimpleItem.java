package wsg.tools.internet.resource.entity.item.impl;

import lombok.Setter;
import wsg.tools.internet.resource.entity.item.base.BaseItem;
import wsg.tools.internet.resource.entity.item.base.TypeSupplier;
import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.entity.item.base.YearSupplier;

import javax.annotation.Nonnull;

/**
 * Items with year and type.
 *
 * @author Kingen
 * @since 2020/10/20
 */
@Setter
public class SimpleItem extends BaseItem implements YearSupplier, TypeSupplier {

    private Integer year;
    private VideoType type;

    public SimpleItem(@Nonnull String url) {
        super(url);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public VideoType getType() {
        return type;
    }

    @Override
    public Integer getYear() {
        return year;
    }
}
