package wsg.tools.internet.info.adult.west;

import wsg.tools.internet.base.view.PathSupplier;

/**
 * A tag on the site.
 *
 * @author Kingen
 * @see PornTubeSite#findAllTags()
 * @see PornTubeSite#findAllByTag(PornTubeTag)
 * @since 2021/4/4
 */
public class PornTubeTag implements PathSupplier {

    private final String path;
    private final String tag;

    PornTubeTag(String path, String tag) {
        this.path = path;
        this.tag = tag;
    }

    @Override
    public String getAsPath() {
        return path;
    }

    public String getTag() {
        return tag;
    }
}
