package wsg.tools.boot.dao.jpa.mapper;

import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.SeasonEntity;

import java.util.List;

/**
 * Repository for seasons of TV series.
 *
 * @author Kingen
 * @since 2020/9/24
 */
public interface SeasonRepository extends BaseRepository<SeasonEntity, Long> {

    /**
     * Find by series id.
     *
     * @param seriesId id of the series
     * @return list of seasons
     */
    List<SeasonEntity> findAllBySeriesId(long seriesId);
}
