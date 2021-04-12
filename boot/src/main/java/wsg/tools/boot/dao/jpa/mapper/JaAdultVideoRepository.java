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
     * Retrieves the first serial number under the domain in descending update time order.
     *
     * @param domain  the domain of the repository
     * @param subtype the subtype of the repository
     * @return optional of the serial number
     */
    @Query(value = "select serial_num from ja_adult_video where domain = ?1 and subtype = ?2 order by timestamp desc limit 1", nativeQuery = true)
    Optional<String> getFirstOrderUpdateTime(@Nonnull String domain, int subtype);

    /**
     * Retrieves the latest update time of the subtype of the domain.
     *
     * @param domain  the domain of the repository
     * @param subtype the subtype of the source
     * @return optional of the maximum rid
     */
    @Query("select max(source.timestamp) from JaAdultVideoEntity where source.domain=?1 and source.subtype=?2")
    Optional<LocalDateTime> getLatestTimestamp(@Nonnull String domain, int subtype);

    /**
     * Retrieves the max subtype of the specified domain.
     *
     * @param domain the domain to be queried
     * @return optional of the maximum subtype
     */
    @Query("select max(source.subtype) from JaAdultVideoEntity where source.domain=?1")
    Optional<Integer> getMaxSubtype(@Nonnull String domain);
}
