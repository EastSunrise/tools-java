package wsg.tools.internet.info.adult.wiki;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;

/**
 * Album including a series of photos.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@Getter
public class WikiAlbum extends WikiAlbumIndex implements UpdateDatetimeSupplier,
    NextSupplier<Integer> {

    private final LocalDateTime updateTime;
    private final List<String> images;

    private List<Integer> relatedCelebrities;
    private List<String> tags;

    private Integer next;

    WikiAlbum(int id, WikiAlbumType type, String title, LocalDateTime updateTime,
        List<String> images) {
        super(id, type, title);
        this.updateTime = updateTime;
        this.images = Collections.unmodifiableList(images);
    }

    void setRelatedCelebrities(List<Integer> relatedCelebrities) {
        this.relatedCelebrities = Collections.unmodifiableList(relatedCelebrities);
    }

    void setTags(List<String> tags) {
        this.tags = Collections.unmodifiableList(tags);
    }

    void setNext(Integer next) {
        this.next = next;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return updateTime;
    }

    @Override
    public Integer nextId() {
        return next;
    }
}
