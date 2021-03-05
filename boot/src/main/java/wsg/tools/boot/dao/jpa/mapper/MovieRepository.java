package wsg.tools.boot.dao.jpa.mapper;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.base.IdView;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;

/**
 * Repository for movies.
 *
 * @author Kingen
 * @since 2020/9/24
 */
@Repository
public interface MovieRepository extends BaseRepository<MovieEntity, Long> {

    /**
     * Query id by db id.
     *
     * @param dbId db id
     * @return id
     */
    Optional<IdView<Long>> findByDbId(Long dbId);

    /**
     * Query id by imdb id.
     *
     * @param imdbId imdb id
     * @return id
     */
    Optional<IdView<Long>> findByImdbId(String imdbId);
}
