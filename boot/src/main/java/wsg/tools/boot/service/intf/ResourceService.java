package wsg.tools.boot.service.intf;

import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.resource.ResourceLinkEntity;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.exception.SiteStatusException;
import wsg.tools.internet.resource.entity.item.base.IdentifiedItem;
import wsg.tools.internet.resource.site.intf.RangeRepository;
import wsg.tools.internet.resource.site.intf.ResourceRepository;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Interface of resource service.
 *
 * @author Kingen
 * @since 2020/11/13
 */
public interface ResourceService {

    /**
     * Import all resources from the given site.
     *
     * @param site an implementation of {@link ResourceRepository}
     * @throws SiteStatusException if the status of the site is abnormal
     */
    <I extends IdentifiedItem, S extends BaseSite & ResourceRepository<I, Integer>> void importAll(S site) throws SiteStatusException;

    /**
     * Import latest resources from the given site.
     *
     * @param site an implementation of {@link ResourceRepository}
     * @throws SiteStatusException if the status of the site is abnormal
     */
    <I extends IdentifiedItem, S extends BaseSite & RangeRepository<I>> void importLatest(S site) throws SiteStatusException;

    /**
     * Search resources of the given key.
     *
     * @param key    key to search
     * @param dbId   id of Douban
     * @param imdbId id of IMDb
     * @return result of list of resources
     */
    List<ResourceItemEntity> search(@Nullable String key, @Nullable Long dbId, @Nullable String imdbId);

    /**
     * Link the given item to the given identifiers.
     *
     * @param checkDtoList arguments
     * @return result of checked count
     */
    long check(List<ResourceCheckDto> checkDtoList);

    /**
     * Obtains links of the given items.
     *
     * @param itemIds ids of items
     * @return map of item-links
     */
    List<ResourceLinkEntity> getLinks(Collection<Long> itemIds);
}
