package wsg.tools.internet.info.adult.west;

import wsg.tools.internet.base.page.BasicPageReq;

/**
 * A request with pagination information for videos on the site.
 *
 * @author Kingen
 * @see PornTubeSite#findPageByCategory(int, PornTubePageReq)
 * @since 2021/4/5
 */
public class PornTubePageReq extends BasicPageReq {

    private static final long serialVersionUID = 645714574677466668L;
    private static final int DEFAULT_SIZE = 15;

    public PornTubePageReq(int current) {
        super(current, DEFAULT_SIZE);
    }

    @Override
    public PornTubePageReq next() {
        return new PornTubePageReq(super.next().getCurrent());
    }

    @Override
    public BasicPageReq previous() {
        return new PornTubePageReq(super.previous().getCurrent());
    }
}
