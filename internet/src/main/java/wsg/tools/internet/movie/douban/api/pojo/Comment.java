package wsg.tools.internet.movie.douban.api.pojo;

import java.time.LocalDateTime;
import lombok.Getter;

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
