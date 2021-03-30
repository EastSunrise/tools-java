package wsg.tools.boot.pojo.entity.adult;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.config.MinioStored;
import wsg.tools.boot.pojo.entity.base.BaseEntity;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.common.io.Filetype;

/**
 * Entity of adult video.
 *
 * @author Kingen
 * @see wsg.tools.internet.info.adult.entry.AbstractAdultEntry
 * @since 2021/2/23
 */
@Getter
@Setter
@Entity
@Table(name = "adult_video")
public class AdultVideoEntity extends BaseEntity {

    private static final long serialVersionUID = 718083190465191530L;

    @Id
    @Column(length = 15)
    private String id;

    @Column(length = 15)
    private String title;

    private Boolean mosaic;

    private Duration duration;

    private LocalDate releaseDate;

    @Column(length = 15)
    private String director;

    @Column(length = 31)
    private String producer;

    @Column(length = 31)
    private String distributor;

    @Column(length = 63)
    private String series;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "adult_video_tag")
    @Column(name = "tag", nullable = false, length = 31)
    private List<String> tags;

    @MinioStored(type = Filetype.IMAGE)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adult_video_image")
    @Column(name = "image", nullable = false, length = 127)
    private List<String> images;

    @Embedded
    private Source source;

    @Column(length = 0)
    private LocalDateTime updateTime;

    public String getId() {
        return id;
    }
}
