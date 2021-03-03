package wsg.tools.internet.resource.site;

import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.resource.item.IdentifiedItem;
import wsg.tools.internet.resource.item.VideoType;
import wsg.tools.internet.resource.item.intf.TypeSupplier;
import wsg.tools.internet.resource.item.intf.UpdateDatetimeSupplier;
import wsg.tools.internet.video.site.douban.DoubanIdentifier;
import wsg.tools.internet.video.site.imdb.ImdbIdentifier;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * Items of {@link BdMovieSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
public class BdMovieItem extends IdentifiedItem implements DoubanIdentifier, ImdbIdentifier, TypeSupplier, UpdateDatetimeSupplier, NextSupplier<Integer> {

    private final LocalDateTime updateTime;
    private Long dbId;
    private String imdbId;
    private Integer next;

    BdMovieItem(int id, @Nonnull String url, LocalDateTime updateTime) {
        super(id, url);
        this.updateTime = updateTime;
    }

    @Override
    public VideoType getType() {
        return VideoType.MOVIE;
    }

    @Override
    public Long getDbId() {
        return dbId;
    }

    void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    @Override
    public String getImdbId() {
        return imdbId;
    }

    void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return updateTime;
    }

    void setNext(Integer next) {
        this.next = next;
    }

    @Override
    public Integer next() {
        return next;
    }
}
