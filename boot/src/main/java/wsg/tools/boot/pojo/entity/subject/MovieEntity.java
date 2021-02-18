package wsg.tools.boot.pojo.entity.subject;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.resource.item.intf.YearSupplier;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.site.douban.DoubanIdentifier;
import wsg.tools.internet.video.site.imdb.ImdbIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Duration;
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
public class MovieEntity extends IdentityEntity implements DoubanIdentifier, ImdbIdentifier, YearSupplier, TitleSupplier {

    @Column(unique = true)
    private Long dbId;

    @Column(unique = true, length = 10)
    private String imdbId;

    @Column(nullable = false, length = 63)
    private String title;

    @Column(length = 127)
    private String text;

    @Column(length = 127)
    private String originalTitle;

    @Column(nullable = false)
    private Integer year;

    @Column(length = 63)
    private List<LanguageEnum> languages;

    @Column(nullable = false, length = 63)
    private List<Duration> durations;
}
