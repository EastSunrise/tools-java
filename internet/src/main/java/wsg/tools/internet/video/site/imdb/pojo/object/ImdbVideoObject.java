package wsg.tools.internet.video.site.imdb.pojo.object;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Object of video on IMDb.
 *
 * @author Kingen
 * @since 2020/9/1
 */
@Getter
@Setter
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