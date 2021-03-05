package wsg.tools.internet.movie.douban.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Reviews of subjects.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
