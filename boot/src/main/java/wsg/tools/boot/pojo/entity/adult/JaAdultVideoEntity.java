package wsg.tools.boot.pojo.entity.adult;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import wsg.tools.boot.config.MinioStored;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.common.io.Filetype;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.SerialNumSupplier;

/**
 * The entity of an adult video.
 *
 * @author Kingen
 * @since 2021/2/23
 */
@Entity
@Table(
    name = "ja_adult_video",
    indexes = @Index(name = "index_ja_adult_serial_num", columnList = "serialNum", unique = true),
    uniqueConstraints = @UniqueConstraint(name = "uni_ja_adult_video_source", columnNames = {
        "sname", "subtype", "rid"
    })
)
public class JaAdultVideoEntity extends IdentityEntity implements SerialNumSupplier,
    TitleSupplier, CoverSupplier, DurationSupplier {

    private static final long serialVersionUID = 718083190465191530L;

    @Column(length = 15)
    private String serialNum;

    @Column
    private String title;

    @Column(length = 127)
    @MinioStored(type = Filetype.IMAGE)
    private String cover;

    private Boolean mosaic;

    private Duration duration;

    private LocalDate publish;

    @Column(length = 63)
    private String producer;

    @Column
    private String distributor;

    @Column(length = 63)
    private String series;

    @JoinTable(
        name = "ja_adult_video_tag",
        joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "tag", referencedColumnName = "tag"),
        foreignKey = @ForeignKey(name = "fk_ja_adult_video_tag_on_video_id"),
        inverseForeignKey = @ForeignKey(name = "fk_ja_adult_video_tag_on_tag")
    )
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<JaAdultTagEntity> tags = new HashSet<>();

    @MinioStored(type = Filetype.IMAGE)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "ja_adult_video_image",
        joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"),
        foreignKey = @ForeignKey(name = "fk_ja_adult_video_image_on_video_id")
    )
    @Column(name = "image", nullable = false, length = 127)
    private Set<String> images = new HashSet<>();

    @JoinTable(
        name = "ja_adult_video_actress",
        joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "actress_id", referencedColumnName = "id"),
        foreignKey = @ForeignKey(name = "fk_ja_adult_video_actress_on_video_id"),
        inverseForeignKey = @ForeignKey(name = "fk_ja_adult_video_actress_on_actress_id")
    )
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<JaAdultActressEntity> actresses = new HashSet<>();

    @Embedded
    private Source source;

    @Override
    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String id) {
        this.serialNum = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public URL getCoverURL() {
        return Optional.ofNullable(cover).map(NetUtils::createURL).orElse(null);
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Boolean getMosaic() {
        return mosaic;
    }

    public void setMosaic(Boolean mosaic) {
        this.mosaic = mosaic;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDate getPublish() {
        return publish;
    }

    public void setPublish(LocalDate release) {
        this.publish = release;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public Set<JaAdultTagEntity> getTags() {
        return tags;
    }

    public void setTags(Set<JaAdultTagEntity> tags) {
        this.tags = tags;
    }

    public Set<String> getImages() {
        return images;
    }

    public void setImages(Set<String> images) {
        this.images = images;
    }

    public Set<JaAdultActressEntity> getActresses() {
        return actresses;
    }

    public void setActresses(Set<JaAdultActressEntity> actresses) {
        this.actresses = actresses;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}
