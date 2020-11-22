package wsg.tools.boot.dao.jpa.mapper;

import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;

import java.util.List;

/**
 * Repository for items of resources.
 *
 * @author Kingen
 * @since 2020/11/4
 */
public interface ResourceItemRepository extends BaseRepository<ResourceItemEntity, Long> {

    /**
     * Find all entities by identify.
     *
     * @param dbId id of Douban
     * @return list of entities
     */
    List<ResourceItemEntity> findAllByDbId(Long dbId);

    /**
     * Find all entities by identify.
     *
     * @param imdbId id of IMDb
     * @return list of entities
     */
    List<ResourceItemEntity> findAllByImdbId(String imdbId);

    /**
     * Find all entities of like title
     *
     * @param title title to match
     * @return list of entities
     */
    List<ResourceItemEntity> findAllByTitleLike(String title);
}
