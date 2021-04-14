package wsg.tools.boot.dao.jpa.mapper;

import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.adult.JaAdultTagEntity;

/**
 * The repository of adult tags.
 *
 * @author Kingen
 * @since 2021/4/12
 */
@Repository
public interface JaAdultTagRepository extends BaseRepository<JaAdultTagEntity, Long> {

    /**
     * Retrieves tag entities of the specified tags.
     *
     * @param tags the tags to be queried
     * @return list of tag entities
     */
    Set<JaAdultTagEntity> findAllByTagIn(List<String> tags);
}
