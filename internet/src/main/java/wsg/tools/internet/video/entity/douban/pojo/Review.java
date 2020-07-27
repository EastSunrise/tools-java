package wsg.tools.internet.video.entity.douban.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Reviews of subjects.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Review {

    private Long id;
    private Long subjectId;
    private String title;
    private Rating rating;
    private String alt;
    private String summary;
    private String content;

    private User author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer usefulCount;
    private Integer uselessCount;
    private Integer commentsCount;
    private String shareUrl;
}
