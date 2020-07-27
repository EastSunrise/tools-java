package wsg.tools.internet.video.entity.douban.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Photos of subjects or celebrities.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Photo {
    private Long id;
    private String subjectId;
    private String alt;
    private User author;
    private LocalDateTime createdAt;
    private String desc;

    private String image;
    private String thumb;
    private String cover;
    private String icon;
    private Integer commentsCount;
    private Integer recsCount;
    private Integer position;

    private Integer photosCount;
    private Long albumId;
    private String albumTitle;
    private String albumUrl;
    private Long prevPhoto;
    private Long nextPhoto;
}
