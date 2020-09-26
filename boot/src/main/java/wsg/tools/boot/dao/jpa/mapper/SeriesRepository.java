package wsg.tools.boot.dao.jpa.mapper;

import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.IdSupplier;
import wsg.tools.boot.pojo.entity.SeriesEntity;

import java.util.Optional;

/**
 * Repository for TV series.
 *
 * @author Kingen
 * @since 2020/9/24
 */
public interface SeriesRepository extends BaseRepository<SeriesEntity, Long> {

    /**
     * Query id by imdb id.
     *
     * @param imdbId imdb id
     * @return id
     */
    Optional<IdSupplier> findByImdbId(String imdbId);
}
