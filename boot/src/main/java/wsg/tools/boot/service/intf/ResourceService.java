package wsg.tools.boot.service.intf;

import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.resource.ResourceLinkEntity;
import wsg.tools.boot.pojo.result.SingleResult;
import wsg.tools.internet.resource.entity.item.base.BaseItem;
import wsg.tools.internet.resource.site.BaseResourceSite;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
     * @param site an implementation of {@link BaseResourceSite<I>}
     */
    <I extends BaseItem> void importAll(BaseResourceSite<I> site);

    /**
     * Search resources of the given key.
     *
     * @param key    key to search
     * @param dbId   id of Douban
     * @param imdbId id of IMDb
     * @return result of list of resources
     */
    Set<ResourceItemEntity> search(@Nullable String key, @Nullable Long dbId, @Nullable String imdbId);

    /**
     * Link the given item to the given identifiers.
     *
     * @param checkDtoList arguments
     * @return result of checked count
     */
    long check(List<ResourceCheckDto> checkDtoList);

    /**
     * Download all resources matched by the given identifiers to the target directory.
     *
     * @param target target directory
     * @param dbId   id of Douban
     * @param imdbId id of IMDb
     * @return -1 if none resource found, otherwise, count of links added to download.
     */
    SingleResult<Long> download(File target, @Nullable Long dbId, @Nullable String imdbId);

    /**
     * Obtains links of the given items.
     *
     * @param itemUrls urls of items
     * @return map of item-links
     */
    List<ResourceLinkEntity> getLinks(Collection<String> itemUrls);
}
