package wsg.tools.boot.pojo.entity.adult;

import java.util.List;
import wsg.tools.boot.pojo.entity.base.EntityView;

/**
 * A view to query tags of {@code AdultVideoEntity} explicitly.
 *
 * @author Kingen
 * @since 2021/3/30
 */
@EntityView
public interface TagsPreview {

    /**
     * Returns the id of the entity
     *
     * @return the id
     */
    String getId();

    /**
     * Returns the tags of the entity.
     *
     * @return the tags
     */
    List<String> getTags();
}
