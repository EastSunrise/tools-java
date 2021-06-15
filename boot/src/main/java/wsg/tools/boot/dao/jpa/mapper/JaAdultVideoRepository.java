package wsg.tools.boot.dao.jpa.mapper;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.adult.JaAdultVideoEntity;

/**
 * A repository of adult videos.
 *
 * @author Kingen
 * @since 2021/3/5
 */
@Repository
public interface JaAdultVideoRepository extends BaseRepository<JaAdultVideoEntity, Long> {

    /**
     * Retrieves the entity of the specified serial number.
     *
     * @param serialNum the serial number to query by
     * @return optional of the entity
     */
    Optional<JaAdultVideoEntity> findBySerialNum(String serialNum);

    /**
     * Retrieves the entity of the specified serial number and id.
     *
     * @param serialNum the serial number to query by
     * @param id        the id to query by
     * @return optional of the entity
     */
    Optional<JaAdultVideoEntity> findBySerialNumAndId(String serialNum, long id);

    /**
     * Retrieves the first entity after the specified one.
     *
     * @param id the id of the specified one
     * @return optional of the next entity
     */
    Optional<JaAdultVideoEntity> getFirstByIdGreaterThan(long id);

    /**
     * Retrieves the first entity before the specified one.
     *
     * @param id the id of the specified one
     * @return optional of the previous entity
     */
    Optional<JaAdultVideoEntity> getFirstByIdLessThanOrderByIdDesc(long id);

    /**
     * Retrieves the latest update time of the subtype of the site.
     *
     * @param sname   the name of the site
     * @param subtype the subtype of the source
     * @return optional of the maximum rid
     */
    @Query("select max(source.timestamp) from JaAdultVideoEntity "
        + "where source.sname=?1 and source.subtype=?2")
    Optional<LocalDateTime> getLatestTimestamp(@Nonnull String sname, int subtype);

    /**
     * Retrieves the max subtype of the specified site.
     *
     * @param sname the name of the site
     * @return optional of the maximum subtype
     */
    @Query("select max(source.subtype) from JaAdultVideoEntity where source.sname=?1")
    Optional<Integer> getMaxSubtype(@Nonnull String sname);
}
