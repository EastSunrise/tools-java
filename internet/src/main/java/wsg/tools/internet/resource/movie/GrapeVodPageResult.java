package wsg.tools.internet.resource.movie;

import java.util.List;
import wsg.tools.internet.base.page.BasicCountablePageResult;

/**
 * A paged result of {@link GrapeVodIndex}.
 *
 * @author Kingen
 * @since 2021/3/9
 */
public class GrapeVodPageResult
    extends BasicCountablePageResult<GrapeVodIndex, GrapeVodPageReq> {

    GrapeVodPageResult(List<GrapeVodIndex> content, GrapeVodPageReq request, long total) {
        super(content, request, total);
    }
}
