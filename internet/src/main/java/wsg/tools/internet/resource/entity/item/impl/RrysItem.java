package wsg.tools.internet.resource.entity.item.impl;

import lombok.Setter;
import wsg.tools.internet.resource.entity.item.base.BaseItem;
import wsg.tools.internet.resource.entity.item.base.TypeSupplier;
import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.site.RrysSite;
import wsg.tools.internet.video.entity.imdb.base.ImdbIdentifier;

import javax.annotation.Nonnull;

/**
 * Items of {@link RrysSite}.
 *
 * @author Kingen
 * @since 2020/12/24
 */
@Setter
public class RrysItem extends BaseItem implements TypeSupplier, ImdbIdentifier {

    private VideoType type;
    private String imdbId;

    public RrysItem(@Nonnull String url) {
        super(url);
    }

    @Override
    public String getImdbId() {
        return imdbId;
    }

    @Override
    public VideoType getType() {
        return type;
    }
}
