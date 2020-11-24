package wsg.tools.boot.pojo.entity.subject;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;
import wsg.tools.internet.video.entity.imdb.base.ImdbIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Movie entity.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Getter
@Setter
@Entity
@Table(name = "video_movie")
public class MovieEntity extends SubjectEntity implements DoubanIdentifier, ImdbIdentifier {

    @Column(unique = true)
    private Long dbId;

    @Column(unique = true, length = 10)
    private String imdbId;

    @Column(length = 127)
    private String text;

    @Column(length = 127)
    private String originalTitle;
}
