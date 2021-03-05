package wsg.tools.boot.dao.jpa.mapper;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;

/**
 * Repository for items of resources.
 *
 * @author Kingen
 * @since 2020/11/4
 */
@Repository
public interface ResourceItemRepository extends BaseRepository<ResourceItemEntity, Long> {

    /**
     * Query max of sid of the given site.
     *
     * @param site name of the site
     * @return max of sid
     */
    @Query("select max(sid) from ResourceItemEntity where site = ?1")
    Optional<Integer> findMaxSid(String site);

    /**
     * Find an entity by the given site key.
     *
     * @param site name of the site
     * @param sid  id under the site
     * @return optional of the entity
     */
    Optional<ResourceItemEntity> findBySiteAndSid(String site, Integer sid);
}
