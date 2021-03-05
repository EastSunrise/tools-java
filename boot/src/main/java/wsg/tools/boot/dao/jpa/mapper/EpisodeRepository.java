package wsg.tools.boot.dao.jpa.mapper;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.subject.EpisodeEntity;

/**
 * Repository for episodes of TV series.
 *
 * @author Kingen
 * @since 2020/9/24
 */
@Repository
public interface EpisodeRepository extends BaseRepository<EpisodeEntity, Long> {

    /**
     * Obtains all episodes of the given season.
     *
     * @param seasonId id of the season
     * @return list of episodes
     */
    List<EpisodeEntity> findAllBySeasonId(Long seasonId);

    /**
     * Query an entity by imdb id.
     *
     * @param imdbId imdb id
     * @return an optional of the entity
     */
    Optional<EpisodeEntity> findByImdbId(String imdbId);
}
