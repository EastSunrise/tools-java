package wsg.tools.internet.video.entity.douban;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Comments of subjects.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@Setter
@Getter
public class Comment {
    private Rating rating;
    private Integer usefulCount;
    private DoubanUser author;
    private Long subjectId;
    private String content;
    private LocalDateTime createdAt;
    private Long id;
}
