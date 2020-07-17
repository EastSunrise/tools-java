package wsg.tools.internet.video.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Reviews of subjects.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@Setter
@Getter
public class Review {
    private Subject.Rating rating;
    private String title;
    private Long subjectId;
    private Subject.User author;
    private String summary;
    private String alt;
    private Long id;
}
