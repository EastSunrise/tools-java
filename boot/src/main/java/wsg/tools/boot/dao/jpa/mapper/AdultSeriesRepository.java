package wsg.tools.boot.dao.jpa.mapper;

import org.springframework.data.jpa.repository.Query;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.AdultSeriesEntity;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for series of adult videos.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public interface AdultSeriesRepository extends BaseRepository<AdultSeriesEntity, Integer> {

    /**
     * Query max of adding time of the items.
     *
     * @return max of time
     */
    @Query("select max(addTime) from AdultSeriesEntity")
    Optional<LocalDateTime> findMaxAddTime();
}
