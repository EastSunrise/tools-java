package wsg.tools.boot.dao.jpa.mapper;

import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.ResourceLinkEntity;

import java.util.List;

/**
 * Repository for links of resources.
 *
 * @author Kingen
 * @since 2020/10/30
 */
public interface ResourceLinkRepository extends BaseRepository<ResourceLinkEntity, Long> {

    /**
     * Obtains all links of the given item.
     *
     * @param itemUrl url of the item
     * @return list of links
     */
    List<ResourceLinkEntity> findAllByItemUrl(String itemUrl);
}
