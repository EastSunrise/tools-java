package wsg.tools.internet.movie.resource;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.base.UpdateDatetimeSupplier;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;

/**
 * Items of {@link BdMovieSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
public class BdMovieItem extends BaseIdentifiedItem
    implements DoubanIdentifier, ImdbIdentifier, UpdateDatetimeSupplier, NextSupplier<Integer> {

    private final BdMovieType type;
    private final LocalDateTime updateTime;
    private Long dbId;
    private String imdbId;
    private Integer next;
    private URL cover;

    BdMovieItem(int id, @Nonnull String url, BdMovieType type, LocalDateTime updateTime) {
        super(id, url);
        this.type = Objects.requireNonNull(type);
        this.updateTime = Objects.requireNonNull(updateTime, "the update time of an item");
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
    public URL getCover() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = Objects.requireNonNull(cover, "the cover of an item");
    }

    @Override
    public Integer nextId() {
        return next;
    }

    @Override
    public int getSubtype() {
        return type.ordinal();
    }
}
