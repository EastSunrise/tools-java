package wsg.tools.boot.pojo.entity.adult;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import wsg.tools.boot.pojo.entity.base.BaseEntity;

/**
 * An entity of an adult tag.
 *
 * @author Kingen
 * @since 2021/4/12
 */
@Entity
@Table(name = "ja_adult_tag")
public class JaAdultTagEntity extends BaseEntity {

    private static final long serialVersionUID = -7194626789319610508L;

    @Id
    @Column(length = 31)
    private String tag;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<JaAdultVideoEntity> videos;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<JaAdultVideoEntity> getVideos() {
        return videos;
    }

    public void setVideos(List<JaAdultVideoEntity> videos) {
        this.videos = videos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JaAdultTagEntity tagEntity = (JaAdultTagEntity) o;
        return Objects.equals(tag, tagEntity.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }
}
