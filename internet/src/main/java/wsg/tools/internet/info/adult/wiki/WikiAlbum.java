package wsg.tools.internet.info.adult.wiki;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import wsg.tools.internet.base.view.NextSupplier;
import wsg.tools.internet.base.view.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.view.AlbumSupplier;
import wsg.tools.internet.info.adult.view.Tagged;

/**
 * Album including a series of photos.
 *
 * @author Kingen
 * @see CelebrityWikiSite#findAlbum(int, WikiAlbumType)
 * @since 2021/2/26
 */
public class WikiAlbum implements WikiAlbumIndex, UpdateDatetimeSupplier, AlbumSupplier, Tagged,
    NextSupplier<Integer> {

    private final int id;
    private final WikiAlbumType type;
    private final String title;
    private LocalDateTime updateTime;
    private List<URL> album;
    private List<Integer> relatedCelebrities;
    private String[] tags;
    private Integer next;

    WikiAlbum(int id, WikiAlbumType type, String title) {
        this.id = id;
        this.type = type;
        this.title = title;
    }

    WikiAlbum(int id, WikiAlbumType type, String title, LocalDateTime updateTime, List<URL> album) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.updateTime = updateTime;
        this.album = Collections.unmodifiableList(album);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public WikiAlbumType getType() {
        return type;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public LocalDateTime getUpdate() {
        return updateTime;
    }

    @Override
    public List<URL> getAlbum() {
        return album;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    void setTags(String[] tags) {
        this.tags = tags;
    }

    public List<Integer> getRelatedCelebrities() {
        return relatedCelebrities;
    }

    void setRelatedCelebrities(List<Integer> relatedCelebrities) {
        this.relatedCelebrities = Collections.unmodifiableList(relatedCelebrities);
    }

    void setNext(Integer next) {
        this.next = next;
    }

    @Override
    public Integer nextId() {
        return next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WikiAlbum wikiAlbum = (WikiAlbum) o;
        return id == wikiAlbum.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
