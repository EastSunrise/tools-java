package wsg.tools.internet.resource.entity.item.impl;

import lombok.Setter;
import wsg.tools.internet.resource.entity.item.base.IdentifiedItem;
import wsg.tools.internet.resource.entity.item.base.TypeSupplier;
import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.entity.item.base.YearSupplier;
import wsg.tools.internet.resource.site.impl.MovieHeavenSite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Items of {@link MovieHeavenSite}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
@Setter
public class MovieHeavenItem extends IdentifiedItem implements TypeSupplier, YearSupplier {

    private Integer year;
    private VideoType type;

    public MovieHeavenItem(int id, @Nonnull String url) {
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
}
