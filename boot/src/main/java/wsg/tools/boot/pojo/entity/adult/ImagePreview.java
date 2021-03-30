package wsg.tools.boot.pojo.entity.adult;

import java.util.List;
import wsg.tools.boot.config.MinioStored;
import wsg.tools.boot.pojo.entity.base.EntityView;
import wsg.tools.common.io.Filetype;

/**
 * A view of an {@code AdultEntity} to preview its images.
 *
 * @author Kingen
 * @since 2021/3/26
 */
@EntityView
public interface ImagePreview {

    /**
     * Returns the id of the entity
     *
     * @return the id
     */
    String getId();

    /**
     * Returns the images of the entity.
     *
     * @return the images
     */
    @MinioStored(type = Filetype.IMAGE)
    List<String> getImages();
}
