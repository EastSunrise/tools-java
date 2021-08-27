package wsg.tools.boot.dao.jpa.mapper;

import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
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
     * Finds the maximum rid of the whole repository.
     *
     * @param sname the name of the site
     * @return optional of the maximum rid
     */
    @Query("select max(source.rid) from ResourceItemEntity where source.sname=?1")
    Optional<Long> findMaxRid(@Nonnull String sname);

    /**
     * Finds the maximum rid of the specified type of the whole repository.
     *
     * @param sname   the name of the site
     * @param subtype the subtype of items to be compared
     * @return optional of the maximum rid
     */
    @Query("select max(source.rid) from ResourceItemEntity "
        + "where source.sname=?1 and source.subtype=?2")
    Optional<Long> findMaxRid(@Nonnull String sname, int subtype);
}
