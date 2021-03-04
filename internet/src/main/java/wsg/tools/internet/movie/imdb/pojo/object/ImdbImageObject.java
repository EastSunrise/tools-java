package wsg.tools.internet.movie.imdb.pojo.object;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

/**
 * Object of images on IMDb.
 *
 * @author Kingen
 * @since 2020/9/1
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("ImageObject")
public class ImdbImageObject {
    private String contentUrl;
}