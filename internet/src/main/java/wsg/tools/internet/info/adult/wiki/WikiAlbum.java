package wsg.tools.internet.info.adult.wiki;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.view.NextSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.view.ImagesSupplier;
import wsg.tools.internet.info.adult.view.Tagged;

/**
 * Album including a series of photos.
 *
 * @author Kingen
 * @see CelebrityWikiSite#findAlbum(WikiAlbumIndex)
 * @since 2021/2/26
 */
public class WikiAlbum implements WikiAlbumIndex, UpdateDatetimeSupplier, ImagesSupplier, Tagged,
    NextSupplier<Integer> {

    private final int id;
    private final WikiAlbumType type;
    private final String title;
    private LocalDateTime updateTime;
    private List<URL> album;
    private List<WikiCelebrityIndex> relatedCelebrities;
    private Set<String> tags = new HashSet<>(0);
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
    public WikiAlbumType getSubtype() {
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

    @Nonnull
    @Override
    public List<URL> getImages() {
        return album;
    }

    @Nonnull
    @Override
    public Set<String> getTags() {
        return tags;
    }

    void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public List<WikiCelebrityIndex> getRelatedCelebrities() {
        return relatedCelebrities;
    }

    void setRelatedCelebrities(List<WikiCelebrityIndex> relatedCelebrities) {
        this.relatedCelebrities = Collections.unmodifiableList(relatedCelebrities);
    }

    void setNext(Integer next) {
        this.next = next;
    }

    @Override
    public Integer getNextId() {
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
