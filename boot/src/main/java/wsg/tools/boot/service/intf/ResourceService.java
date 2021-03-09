package wsg.tools.boot.service.intf;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.http.client.HttpResponseException;
import wsg.tools.boot.common.util.OtherHttpResponseException;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.service.impl.ResourceDto;
import wsg.tools.internet.base.intf.IntRangeIdentifiedRepository;
import wsg.tools.internet.base.intf.LinkedRepository;
import wsg.tools.internet.common.NextSupplier;
import wsg.tools.internet.resource.movie.IdentifiedItem;

/**
 * Interface of resource service.
 *
 * @author Kingen
 * @since 2020/11/13
 */
public interface ResourceService {

    /**
     * Imports latest resources from {@link LinkedRepository}.
     *
     * @param repository the target repository
     * @param domain     the domain of the repository
     * @param subtype    the subtype whose records are going to import
     * @throws OtherHttpResponseException if an unexpected {@link HttpResponseException} occurs
     */
    <T extends IdentifiedItem & NextSupplier<Integer>> void importLinkedRepository(
        LinkedRepository<Integer, T> repository, String domain, int subtype)
        throws OtherHttpResponseException;

    /**
     * Imports latest resources from {@link IntRangeIdentifiedRepository}.
     *
     * @param repository the target repository
     * @param domain     the domain of the repository
     * @param subtype    the subtype whose records are going to import
     * @throws OtherHttpResponseException if an unexpected {@link HttpResponseException} occurs
     */
    <T extends IdentifiedItem> void importIntRangeRepository(
        IntRangeIdentifiedRepository<T> repository, String domain, int subtype)
        throws OtherHttpResponseException;

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
