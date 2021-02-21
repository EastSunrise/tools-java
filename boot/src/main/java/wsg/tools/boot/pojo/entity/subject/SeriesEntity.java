package wsg.tools.boot.pojo.entity.subject;

import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.video.enums.LanguageEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

/**
 * Entity of TV series.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Entity
@Table(name = "video_series")
public class SeriesEntity extends IdentityEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String imdbId;

    @Column(nullable = false, length = 63)
    private String zhTitle;

    @Column(nullable = false, length = 127)
    private String enTitle;

    @Column(nullable = false)
    private Integer year;

    @Column(length = 63)
    private List<LanguageEnum> languages;

    @Column(nullable = false)
    private Integer seasonsCount;

    public String getImdbId() {
        return imdbId;
    }

    public String getZhTitle() {
        return zhTitle;
    }

    public String getEnTitle() {
        return enTitle;
    }

    public Integer getYear() {
        return year;
    }

    public List<LanguageEnum> getLanguages() {
        return languages;
    }

    public Integer getSeasonsCount() {
        return seasonsCount;
    }
}
