package wsg.tools.boot.dao.jpa.mapper;

import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.base.IdView;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;

import java.util.List;
import java.util.Optional;

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

    /**
     * Query id by db id.
     *
     * @param dbId db id
     * @return id
     */
    Optional<IdView<Long>> findByDbId(Long dbId);
}
