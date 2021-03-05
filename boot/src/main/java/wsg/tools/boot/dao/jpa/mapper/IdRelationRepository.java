package wsg.tools.boot.dao.jpa.mapper;

import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.subject.IdRelationEntity;

/**
 * Repository for query relations between ids.
 *
 * @author Kingen
 * @since 2020/9/22
 */
@Repository
public interface IdRelationRepository extends BaseRepository<IdRelationEntity, String> {

}
