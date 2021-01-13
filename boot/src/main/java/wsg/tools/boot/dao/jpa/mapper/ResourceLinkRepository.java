package wsg.tools.boot.dao.jpa.mapper;

import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.resource.ResourceLinkEntity;

import java.util.Collection;
import java.util.List;

/**
 * Repository for links of resources.
 *
 * @author Kingen
 * @since 2020/10/30
 */
public interface ResourceLinkRepository extends BaseRepository<ResourceLinkEntity, Long> {

    /**
     * Obtains all links of the given items.
     *
     * @param itemIds ids of the items
     * @return list of links
     */
    List<ResourceLinkEntity> findAllByItemIdIsIn(Collection<Long> itemIds);
}
