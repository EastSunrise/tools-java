package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.IdentityEntity;
import wsg.tools.internet.video.enums.LanguageEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Duration;
import java.time.Year;
import java.util.List;

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
public class MovieEntity extends IdentityEntity {

    @Column(unique = true)
    private Long dbId;

    @Column(unique = true, length = 10)
    private String imdbId;

    @Column(length = 63)
    private String title;

    @Column(length = 127)
    private String text;

    @Column(length = 127)
    private String originalTitle;

    @Column(nullable = false)
    private Year year;

    @Column(nullable = false, length = 63)
    private List<LanguageEnum> languages;

    @Column(nullable = false, length = 63)
    private List<Duration> durations;
}
