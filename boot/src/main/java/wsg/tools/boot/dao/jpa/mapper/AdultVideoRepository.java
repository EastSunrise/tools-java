package wsg.tools.boot.dao.jpa.mapper;

import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity;

/**
 * Repository of adult videos.
 *
 * @author Kingen
 * @since 2021/3/5
 */
@Repository
public interface AdultVideoRepository extends BaseRepository<AdultVideoEntity, String> {

    /**
     * Finds the maximum rid of the subtype of the repository.
     *
     * @param repository the repository of the source
     * @param subtype    the subtype of the source
     * @return optional of the maximum rid
     */
    @Query("select max(source.rid) from AdultVideoEntity "
        + "where source.repository=?1 and source.subtype=?2")
    Optional<Long> findMaxRid(@Nonnull String repository, int subtype);
}
