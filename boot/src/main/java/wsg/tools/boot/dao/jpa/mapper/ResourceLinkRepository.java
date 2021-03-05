package wsg.tools.boot.dao.jpa.mapper;

import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.resource.ResourceLinkEntity;

/**
 * Repository for links of resources.
 *
 * @author Kingen
 * @since 2020/10/30
 */
@Repository
public interface ResourceLinkRepository extends BaseRepository<ResourceLinkEntity, Long> {

    /**
     * Obtains all links of the given items.
     *
     * @param itemIds ids of the items
     * @return list of links
     */
    List<ResourceLinkEntity> findAllByItemIdIsIn(Collection<Long> itemIds);
}
