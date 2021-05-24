package wsg.tools.internet.movie.douban;

import java.util.List;
import wsg.tools.internet.base.page.FixedSizePageReq;
import wsg.tools.internet.base.page.FixedSizePageResult;

/**
 * The paged result of Douban subjects or persons.
 *
 * @author Kingen
 * @since 2021/5/16
 */
public class DoubanPageResult<T> extends FixedSizePageResult<T, FixedSizePageReq> {

    DoubanPageResult(List<T> content, FixedSizePageReq request, long total, int pageSize) {
        super(content, request, total, pageSize);
    }
}
