package wsg.tools.boot.dao.jpa.mapper;

import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;

/**
 * Repository for items of resources.
 *
 * @author Kingen
 * @since 2020/11/4
 */
@Repository
public interface ResourceItemRepository extends BaseRepository<ResourceItemEntity, Long> {

    /**
     * Finds the maximum rid of the subtype of the repository.
     *
     * @param domain  the domain of the repository
     * @param subtype the subtype of the source
     * @return optional of the maximum rid
     */
    @Query("select max(source.rid) from ResourceItemEntity "
        + "where source.domain=?1 and source.subtype=?2")
    Optional<Long> findMaxRid(@Nonnull String domain, int subtype);

    /**
     * Finds a record by the given source. All properties of the source will be used.
     *
     * @param source the source of the record
     * @return optional of the record
     */
    Optional<ResourceItemEntity> findBySource(Source source);
}
