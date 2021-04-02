package wsg.tools.boot.pojo.entity.subject;

import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.common.enums.Language;

/**
 * Entity of TV series.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Entity
@Table(
    name = "video_series",
    indexes = @Index(name = "unique_series_imdb", columnList = "imdbId", unique = true)
)
public class SeriesEntity extends IdentityEntity {

    private static final long serialVersionUID = 8249371344171811042L;

    @Column(nullable = false, unique = true, length = 10)
    private String imdbId;

    @Column(nullable = false, length = 63)
    private String zhTitle;

    @Column(nullable = false, length = 127)
    private String enTitle;

    @Column(nullable = false)
    private Integer year;

    @Column(length = 63)
    private List<Language> languages;

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

    public List<Language> getLanguages() {
        return Collections.unmodifiableList(languages);
    }

    public Integer getSeasonsCount() {
        return seasonsCount;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
