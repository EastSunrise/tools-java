package wsg.tools.internet.info.adult.west;

import java.util.Objects;

/**
 * A category on the site.
 *
 * @author Kingen
 * @see PornTubeSite#findAllCategories()
 * @see PornTubeSite#findPageByCategory(int, PornTubePageReq)
 * @since 2021/4/1
 */
public class PornTubeCategory {

    private final int id;
    private final String title;
    private final int videos;

    PornTubeCategory(int id, String title, int videos) {
        this.id = id;
        this.title = title;
        this.videos = videos;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getVideos() {
        return videos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PornTubeCategory category = (PornTubeCategory) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
