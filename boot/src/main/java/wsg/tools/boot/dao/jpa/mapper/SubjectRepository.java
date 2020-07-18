package wsg.tools.boot.dao.jpa.mapper;

import org.springframework.data.jpa.repository.Query;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.SubjectEntity;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface of subjects.
 *
 * @author Kingen
 * @since 2020/7/11
 */
public interface SubjectRepository extends BaseRepository<SubjectEntity, Long> {

    /**
     * Query max of tag date
     *
     * @return max of tag date
     */
    @Query("select max(markDate) from SubjectEntity")
    LocalDate findMaxMarkDate();

    /**
     * Query by db id.
     *
     * @param dbId db id
     * @return result
     */
    Optional<SubjectEntity> findByDbId(Long dbId);

    /**
     * Query by imdb id.
     *
     * @param imdbId imdb id
     * @return result
     */
    Optional<SubjectEntity> findByImdbId(String imdbId);
}
