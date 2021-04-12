package wsg.tools.boot.pojo.entity.adult;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import wsg.tools.boot.config.MinioStored;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.common.io.Filetype;
import wsg.tools.common.net.NetUtils;
import wsg.tools.internet.info.adult.view.AlbumSupplier;
import wsg.tools.internet.info.adult.view.JaAdultEntry;
import wsg.tools.internet.info.adult.view.Tagged;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;

/**
 * The entity of an adult video.
 *
 * @author Kingen
 * @since 2021/2/23
 */
@Entity
@Table(name = "ja_adult_video")
public class JaAdultVideoEntity extends IdentityEntity implements JaAdultEntry, TitledAdultEntry,
    Tagged, AlbumSupplier {

    private static final long serialVersionUID = 718083190465191530L;

    @Column(nullable = false, unique = true, length = 15)
    private String serialNum;

    @Column(length = 127)
    private String title;

    @Column(length = 127)
    @MinioStored(type = Filetype.IMAGE)
    private String cover;

    private Boolean mosaic;

    private Duration duration;

    private LocalDate publish;

    @Column(length = 63)
    private String producer;

    @Column(length = 63)
    private String distributor;

    @Column(length = 63)
    private String series;

    @Column(length = 127)
    private String[] tags;

    @MinioStored(type = Filetype.IMAGE)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adult_video_image")
    @Column(name = "image", nullable = false, length = 127)
    private List<String> images;

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

    @Override
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

    @Override
    public LocalDate getRelease() {
        return publish;
    }

    public void setRelease(LocalDate release) {
        this.publish = release;
    }

    @Override
    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    @Override
    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    @Override
    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public List<URL> getAlbum() {
        return images.stream().map(NetUtils::createURL).collect(Collectors.toList());
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}
