package wsg.tools.internet.resource.movie;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.NextSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;

/**
 * Items of {@link BdMovieSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
public class BdMovieItem extends IdentifiedItem<BdMovieType>
    implements DoubanIdentifier, ImdbIdentifier, UpdateDatetimeSupplier, NextSupplier<Integer> {

    private final LocalDateTime updateTime;
    private Long dbId;
    private String imdbId;
    private Integer next;

    BdMovieItem(int id, @Nonnull String url, BdMovieType type, LocalDateTime updateTime) {
        super(id, url, type);
        this.updateTime = updateTime;
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
    public Integer nextId() {
        return next;
    }
}
