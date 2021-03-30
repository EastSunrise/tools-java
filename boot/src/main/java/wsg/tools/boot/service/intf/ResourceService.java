package wsg.tools.boot.service.intf;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.service.impl.ResourceDto;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.resource.movie.BaseIdentifiedItem;

/**
 * Interface of resource service.
 *
 * @author Kingen
 * @since 2020/11/13
 */
public interface ResourceService {

    /**
     * Imports latest resources from a {@link ListRepository} whose entities support integer
     * identifiers and can be classified.
     *
     * @param repository the target repository
     * @param domain     the domain of the repository
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    <T extends BaseIdentifiedItem>
    void importIntListRepository(ListRepository<Integer, T> repository, String domain)
        throws OtherResponseException;

    /**
     * Search resources of the given key.
     *
     * @param key    key to search
     * @param dbId   id of Douban
     * @param imdbId id of IMDb
     * @return result of list of resources
     */
    List<ResourceItemEntity> search(@Nullable String key, @Nullable Long dbId,
        @Nullable String imdbId);

    /**
     * Search resources of the given key.
     *
     * @param key        key to search
     * @param dbId       id of Douban
     * @param imdbId     id of IMDb
     * @param identified if identified
     * @return result of list of resources
     */
    List<ResourceItemEntity> search(@Nullable String key, @Nullable Long dbId,
        @Nullable String imdbId, @Nullable Boolean identified);

    /**
     * Link the given item to the given identifiers.
     *
     * @param checkDtoList arguments
     * @return result of checked count
     */
    long identifyResources(List<ResourceCheckDto> checkDtoList);

    /**
     * Obtains links of the given items.
     *
     * @param items items
     * @return map of item-links
     */
    List<ResourceDto> getResources(Collection<ResourceItemEntity> items);
}
