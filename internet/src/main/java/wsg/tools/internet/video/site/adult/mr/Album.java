package wsg.tools.internet.video.site.adult.mr;

import lombok.Getter;
import wsg.tools.internet.resource.item.intf.UpdateDatetimeSupplier;
import wsg.tools.internet.video.site.adult.mr.enums.AlbumType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Album including a series of photos.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@Getter
public class Album extends SimpleAlbum implements UpdateDatetimeSupplier {

    private final LocalDateTime updateTime;
    private final List<String> images;

    private List<Integer> relatedCelebrities;
    private List<String> tags;

    Album(int id, AlbumType type, String title, LocalDateTime updateTime, List<String> images) {
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

    @Override
    public LocalDateTime lastUpdate() {
        return updateTime;
    }
}
