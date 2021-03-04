package wsg.tools.internet.movie.douban.api.pojo;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Short comments of subjects.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@Getter
public class Comment {
    private Rating rating;
    private Integer usefulCount;
    private User author;
    private Long subjectId;
    private String content;
    private LocalDateTime createdAt;
    private Long id;
}
