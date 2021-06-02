package wsg.tools.internet.movie.resource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import wsg.tools.internet.base.view.NextSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.support.InvalidResourceException;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;

/**
 * Items of {@link BdMovieSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
public class BdMovieItem extends BaseIdentifiedItem<BdMovieType>
    implements DoubanIdentifier, ImdbIdentifier, UpdateDatetimeSupplier, NextSupplier<Integer> {

    private final LocalDateTime updateTime;
    private Long dbId;
    private String imdbId;
    private Integer next;

    BdMovieItem(int id, BdMovieType subtype, String title, List<Link> links,
        List<InvalidResourceException> exceptions, LocalDateTime updateTime) {
        super(id, subtype, title, links, exceptions);
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
    public LocalDateTime getUpdate() {
        return updateTime;
    }

    void setNext(Integer next) {
        this.next = next;
    }

    @Override
    public Integer getNextId() {
        return next;
    }
}
