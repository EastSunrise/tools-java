package wsg.tools.internet.movie.imdb.pojo.object;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Object of video on IMDb.
 *
 * @author Kingen
 * @since 2020/9/1
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("VideoObject")
public class ImdbVideoObject {

    private String name;
    private String embedUrl;
    private String description;
    private ImdbImageObject thumbnail;
    private LocalDateTime uploadDate;
    private String thumbnailUrl;
}