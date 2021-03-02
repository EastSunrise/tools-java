package wsg.tools.boot.service.intf;

import org.apache.http.client.HttpResponseException;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.service.impl.ResourceDto;
import wsg.tools.internet.base.RecordIterator;
import wsg.tools.internet.base.intf.RangeRepository;
import wsg.tools.internet.base.intf.Repository;
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
     * Import all resources from the given iterator.
     *
     * @param iterator     iterator over the resources
     * @param repositoryId the identifier of the repository
     * @throws HttpResponseException if an error occurs when do request
     */
    <T extends IdentifiedItem>
    void importIterator(RecordIterator<T> iterator, String repositoryId) throws HttpResponseException;

    /**
     * Import all resources from the given repository.
     *
     * @param repository   an implementation of {@link RangeRepository}
     * @param repositoryId the identifier of the repository
     * @throws HttpResponseException if an error occurs when do request
     */
    <T extends IdentifiedItem, R extends Repository<Integer, T> & RangeRepository<T, Integer>>
    void importRangeRepository(R repository, String repositoryId) throws HttpResponseException;

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
