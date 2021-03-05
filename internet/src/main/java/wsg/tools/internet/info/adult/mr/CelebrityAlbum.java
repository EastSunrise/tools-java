package wsg.tools.internet.info.adult.mr;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import wsg.tools.internet.common.NextSupplier;
import wsg.tools.internet.resource.common.UpdateDatetimeSupplier;

/**
 * Album including a series of photos.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@Getter
public class CelebrityAlbum extends SimpleAlbum implements UpdateDatetimeSupplier, NextSupplier<Integer> {

    private final LocalDateTime updateTime;
    private final List<String> images;

    private List<Integer> relatedCelebrities;
    private List<String> tags;

    private Integer next;

    CelebrityAlbum(int id, AlbumType type, String title, LocalDateTime updateTime, List<String> images) {
        super(id, type, title);
        this.updateTime = updateTime;
        this.images = images;
    }

    void setRelatedCelebrities(List<Integer> relatedCelebrities) {
        this.relatedCelebrities = relatedCelebrities;
    }

    void setTags(List<String> tags) {
        this.tags = tags;
    }

    void setNext(Integer next) {
        this.next = next;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return updateTime;
    }

    @Override
    public Integer next() {
        return next;
    }
}
