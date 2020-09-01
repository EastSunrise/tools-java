package wsg.tools.internet.video.entity.imdb.object;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

/**
 * Object of images on IMDb.
 *
 * @author Kingen
 * @since 2020/9/1
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("ImageObject")
public class ImdbImageObject {
    private String contentUrl;
}