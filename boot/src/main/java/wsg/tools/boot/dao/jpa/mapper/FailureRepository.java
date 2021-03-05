package wsg.tools.boot.dao.jpa.mapper;

import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.base.Failure;

/**
 * Repository of failures.
 *
 * @author Kingen
 * @since 2021/3/6
 */
@Repository
public interface FailureRepository extends BaseRepository<Failure, Long> {

}
