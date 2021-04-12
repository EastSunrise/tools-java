package wsg.tools.boot.dao.jpa.mapper;

import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.adult.WesternAdultVideoEntity;

/**
 * A repository of western adult videos.
 *
 * @author Kingen
 * @since 2021/4/10
 */
@Repository
public interface WesternAdultVideoRepository extends BaseRepository<WesternAdultVideoEntity, Long> {

    /**
     * Retrieves the max rid of the specified domain.
     *
     * @param domain  the domain to be queried
     * @param subtype the subtype to be queried
     * @return optional of the maximum rid
     */
    @Query("select max(source.rid) from WesternAdultVideoEntity where source.domain=?1 and source.subtype=?2")
    Optional<Long> getMaxRid(@Nonnull String domain, int subtype);
}
