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
    @Query("select max(tagDate) from SubjectEntity")
    LocalDate findMaxTagDate();

    /**
     * Query by db id and imdb id.
     *
     * @param dbId   db id
     * @param imdbId IMDb id
     * @return result
     */
    @Query("from SubjectEntity where (dbId is null or dbId = ?1) and (imdbId is null or imdbId = ?2)")
    Optional<SubjectEntity> findByDbIdAndImdbId(Long dbId, String imdbId);
}
