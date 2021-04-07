package wsg.tools.internet.info.adult.west;

import java.util.List;
import wsg.tools.internet.base.page.CountablePageResult;

/**
 * A paged result of simple videos.
 *
 * @author Kingen
 * @see PornTubeSite#findPageByCategory(int, PornTubePageReq)
 * @since 2021/4/5
 */
public class PornTubePageResult extends CountablePageResult<PornTubeSimpleVideo, PornTubePageReq> {

    PornTubePageResult(List<PornTubeSimpleVideo> content, PornTubePageReq request, int totalPages) {
        super(content, request, totalPages);
    }
}
