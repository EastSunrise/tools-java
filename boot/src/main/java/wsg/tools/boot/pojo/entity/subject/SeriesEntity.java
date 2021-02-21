package wsg.tools.boot.pojo.entity.subject;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.resource.item.intf.YearSupplier;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.site.imdb.ImdbIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

/**
 * Entity of TV series.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Getter
@Entity
@Table(name = "video_series")
public class SeriesEntity extends IdentityEntity implements ImdbIdentifier, YearSupplier {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SeriesEntity that = (SeriesEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
