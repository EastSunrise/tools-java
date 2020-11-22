package wsg.tools.boot.dao.jpa.mapper;

import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.EpisodeEntity;

import java.util.List;

/**
 * Repository for episodes of TV series.
 *
 * @author Kingen
 * @since 2020/9/24
 */
public interface EpisodeRepository extends BaseRepository<EpisodeEntity, Long> {

    /**
     * Obtains all episodes of the given season.
     *
     * @param seasonId id of the season
     * @return list of episodes
     */
    List<EpisodeEntity> findAllBySeasonId(Long seasonId);
}
