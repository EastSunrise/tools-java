package wsg.tools.internet.resource.entity.item.impl;

import lombok.Setter;
import wsg.tools.internet.resource.entity.item.base.BaseItem;
import wsg.tools.internet.resource.entity.item.base.TypeSupplier;
import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.site.BdFilmSite;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;
import wsg.tools.internet.video.entity.imdb.base.ImdbIdentifier;

import javax.annotation.Nonnull;

/**
 * Items of {@link BdFilmSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
@Setter
public class BdFilmItem extends BaseItem implements DoubanIdentifier, ImdbIdentifier, TypeSupplier {

    private Long dbId;
    private String imdbId;

    public BdFilmItem(@Nonnull String url) {
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
        return VideoType.MOVIE;
    }

    @Override
    public Long getDbId() {
        return dbId;
    }

    @Override
    public String getImdbId() {
        return imdbId;
    }
}
