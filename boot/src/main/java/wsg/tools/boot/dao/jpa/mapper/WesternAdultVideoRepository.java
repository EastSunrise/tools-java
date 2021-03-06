package wsg.tools.boot.dao.jpa.mapper;

import java.time.LocalDateTime;
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
     * Retrieves the max rid of the specified site.
     *
     * @param sname   the name of the site to be queried
     * @param subtype the subtype to be queried
     * @return optional of the maximum rid
     */
    @Query("select max(source.rid) from WesternAdultVideoEntity where source.sname=?1 and source.subtype=?2")
    Optional<Long> getMaxRid(@Nonnull String sname, int subtype);

    /**
     * Retrieves the latest update time of the subtype of the site.
     *
     * @param sname   the name of the site
     * @param subtype the subtype of the source
     * @return optional of the maximum rid
     */
    @Query("select max(source.timestamp) from WesternAdultVideoEntity where source.sname=?1 and source.subtype=?2")
    Optional<LocalDateTime> getLatestTimestamp(@Nonnull String sname, int subtype);
}
