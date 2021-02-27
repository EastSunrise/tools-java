package wsg.tools.boot.service.intf;

import org.apache.http.client.HttpResponseException;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.service.impl.ResourceDto;
import wsg.tools.internet.base.BaseRepository;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.RangeRepository;
import wsg.tools.internet.resource.item.IdentifiedItem;

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
     * @param site an implementation of {@link BaseRepository}
     * @throws HttpResponseException if an error occurs when do request
     */
    <T extends IdentifiedItem, S extends BaseSite & BaseRepository<Integer, T>> void importAll(S site) throws HttpResponseException;

    /**
     * Import latest resources from the given site.
     *
     * @param site an implementation of {@link BaseRepository}
     * @throws HttpResponseException if an error occurs when do request
     */
    <T extends IdentifiedItem, S extends BaseSite & RangeRepository<T, Integer>> void importLatest(S site) throws HttpResponseException;

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
     * Search resources of the given key.
     *
     * @param key        key to search
     * @param dbId       id of Douban
     * @param imdbId     id of IMDb
     * @param identified if identified
     * @return result of list of resources
     */
    List<ResourceItemEntity> search(@Nullable String key, @Nullable Long dbId, @Nullable String imdbId, @Nullable Boolean identified);

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
     * @param items items
     * @return map of item-links
     */
    List<ResourceDto> getResources(Collection<ResourceItemEntity> items);
}
