package wsg.tools.boot.dao.jpa.mapper;

import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.SubjectEntity;

import java.util.Optional;

/**
 * Repository interface of subjects.
 *
 * @author Kingen
 * @since 2020/7/11
 */
public interface SubjectRepository extends BaseRepository<SubjectEntity, Long> {

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
