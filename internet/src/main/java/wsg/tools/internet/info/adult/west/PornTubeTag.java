package wsg.tools.internet.info.adult.west;

import java.util.Objects;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.view.PathSupplier;

/**
 * A tag on the site.
 *
 * @author Kingen
 * @see PornTubeSite#findAllTags()
 * @see PornTubeSite#findAllByTag(PornTubeTag)
 * @since 2021/4/4
 */
public class PornTubeTag implements PathSupplier, TitleSupplier {

    private final String titlePath;
    private final String title;

    PornTubeTag(String titlePath, String title) {
        this.titlePath = titlePath;
        this.title = title;
    }

    @Override
    public String getAsPath() {
        return titlePath;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PornTubeTag tag = (PornTubeTag) o;
        return titlePath.equals(tag.titlePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titlePath);
    }
}
