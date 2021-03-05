package wsg.tools.boot.dao.jpa.mapper;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity;

/**
 * Repository of adult videos.
 *
 * @author Kingen
 * @since 2021/3/5
 */
@Repository
public interface AdultVideoRepository extends BaseRepository<AdultVideoEntity, String> {

    /**
     * Query the latest created record of the given source.
     *
     * @param source the source of the records
     * @return the id of the latest created record
     */
    @Query("select id from AdultVideoEntity where source = ?1 order by gmtCreated DESC")
    Optional<Integer> findLatestCreated(String source);

}
