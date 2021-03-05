package wsg.tools.boot.dao.jpa.mapper;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;

/**
 * Repository for TV series.
 *
 * @author Kingen
 * @since 2020/9/24
 */
@Repository
public interface SeriesRepository extends BaseRepository<SeriesEntity, Long> {

    /**
     * Query target entity by the given imdb id.
     *
     * @param imdbId imdb id
     * @return optional entity
     */
    Optional<SeriesEntity> findByImdbId(String imdbId);
}
