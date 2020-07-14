package wsg.tools.boot.dao.jpa.mapper;

import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.SubjectEntity;

/**
 * Repository interface of subjects.
 *
 * @author Kingen
 * @since 2020/7/11
 */
@Repository
public interface SubjectRepository extends BaseRepository<SubjectEntity, Long> {
}
