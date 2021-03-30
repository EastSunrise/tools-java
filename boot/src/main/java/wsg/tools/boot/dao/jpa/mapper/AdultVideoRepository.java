package wsg.tools.boot.dao.jpa.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.adult.AdultVideoEntity;
import wsg.tools.boot.pojo.entity.adult.ImagePreview;
import wsg.tools.boot.pojo.entity.base.IdView;

/**
 * Repository of adult videos.
 *
 * @author Kingen
 * @since 2021/3/5
 */
@Repository
public interface AdultVideoRepository extends BaseRepository<AdultVideoEntity, String> {

    /**
     * Retrieves the first identifier under the domain in descending update time order.
     *
     * @param domain the domain of the repository
     * @return optional of the view of the identifier
     */
    @Query(value = "select id from adult_video where domain = ?1 order by update_time desc limit 1", nativeQuery = true)
    Optional<IdView<String>> getFirstOrderUpdateTime(@Nonnull String domain);

    /**
     * Retrieves the latest update time of the subtype of the domain.
     *
     * @param domain  the domain of the repository
     * @param subtype the subtype of the source
     * @return optional of the maximum rid
     */
    @Query("select max(updateTime) from AdultVideoEntity "
        + "where source.domain=?1 and source.subtype=?2")
    Optional<LocalDateTime> findLatestUpdate(@Nonnull String domain, int subtype);

    /**
     * Retrieves all views of the entity with images.
     *
     * @return the list of views
     */
    List<ImagePreview> findAllByImagesIsNotNullOrderByGmtModified();

    /**
     * Retrieves tags of the video of the given identifier.
     *
     * @param id the identifier of the video to query
     * @return view of tags
     */
    @Query(value = "select tag from adult_video_tag where adult_video_entity_id=?1", nativeQuery = true)
    List<String> findTagsById(String id);
}
