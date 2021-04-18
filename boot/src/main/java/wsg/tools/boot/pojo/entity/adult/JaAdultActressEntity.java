package wsg.tools.boot.pojo.entity.adult;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import wsg.tools.boot.config.MinioStored;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.common.io.Filetype;
import wsg.tools.internet.common.enums.Region;
import wsg.tools.internet.info.adult.common.CupEnum;

/**
 * The entity of an adult actress.
 *
 * @author Kingen
 * @since 2021/3/4
 */
@Entity
@Table(
    name = "ja_adult_actress",
    uniqueConstraints = {
        @UniqueConstraint(name = "ja_adult_actress_name", columnNames = "name"),
        @UniqueConstraint(name = "ja_adult_actress_zh_name", columnNames = "zhName"),
        @UniqueConstraint(name = "ja_adult_actress_en_name", columnNames = "enName")
    }
)
public class JaAdultActressEntity extends IdentityEntity {

    private static final long serialVersionUID = -8203837165615636026L;

    @Column(nullable = false, length = 31)
    private String name;

    @Column(length = 7)
    private String zhName;

    @Column(length = 15)
    private String enName;

    @CollectionTable(
        name = "ja_adult_actress_aka",
        joinColumns = @JoinColumn(name = "actress_id", referencedColumnName = "id", nullable = false),
        foreignKey = @ForeignKey(name = "fk_ja_adult_actress_aka_on_actress_id")
    )
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "name", nullable = false, length = 31)
    private Set<String> aka = new HashSet<>();

    @CollectionTable(
        name = "ja_adult_actress_image",
        joinColumns = @JoinColumn(name = "actress_id", referencedColumnName = "id", nullable = false),
        foreignKey = @ForeignKey(name = "fk_ja_adult_actress_image_on_actress_id")
    )
    @MinioStored(type = Filetype.IMAGE)
    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "image", nullable = false, length = 127)
    private Set<String> images = new HashSet<>();

    @Column
    private Integer height;

    @Column
    private Integer weight;

    @Column(length = 1)
    @Enumerated(EnumType.STRING)
    private CupEnum cup;

    @Column
    private LocalDate birthday;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate retireDate;

    @Column(length = 3)
    private Region region;

    @ManyToMany(mappedBy = "actresses", fetch = FetchType.LAZY)
    private List<JaAdultVideoEntity> videos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public Set<String> getAka() {
        return aka;
    }

    public void setAka(Set<String> aka) {
        this.aka = aka;
    }

    public Set<String> getImages() {
        return images;
    }

    public void setImages(Set<String> images) {
        this.images = images;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public CupEnum getCup() {
        return cup;
    }

    public void setCup(CupEnum cup) {
        this.cup = cup;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getRetireDate() {
        return retireDate;
    }

    public void setRetireDate(LocalDate retireDate) {
        this.retireDate = retireDate;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
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
        if (!super.equals(o)) {
            return false;
        }
        JaAdultActressEntity entity = (JaAdultActressEntity) o;
        return name.equals(entity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
