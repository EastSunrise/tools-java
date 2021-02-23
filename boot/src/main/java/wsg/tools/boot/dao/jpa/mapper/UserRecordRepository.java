package wsg.tools.boot.dao.jpa.mapper;

import org.springframework.data.jpa.repository.Query;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.UserRecordEntity;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository for user-subject relationship.
 *
 * @author Kingen
 * @since 2020/8/1
 */
public interface UserRecordRepository extends BaseRepository<UserRecordEntity, Long> {

    /**
     * Query max of tag date
     *
     * @param userId identifier of the user
     * @return max of tag date
     */
    @Query("select max(markDate) from UserRecordEntity where userId = ?1")
    Optional<LocalDate> findMaxMarkDate(long userId);

    /**
     * Obtains an entity with the given subject id and user id.
     *
     * @param subjectId id of subject
     * @param userId    id of user
     * @return option of target entity
     */
    Optional<UserRecordEntity> findBySubjectIdAndUserId(Long subjectId, Long userId);
}
