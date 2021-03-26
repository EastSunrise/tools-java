package wsg.tools.internet.resource.movie;

import java.util.List;
import wsg.tools.internet.base.page.BasicCountablePageResult;

/**
 * A paged result of {@link GrapeVodIndex}.
 *
 * @author Kingen
 * @since 2021/3/9
 */
public class GrapeVodPageResult extends BasicCountablePageResult<GrapeVodIndex> {

    GrapeVodPageResult(List<GrapeVodIndex> content, GrapeVodPageRequest request, long total) {
        super(content, request, total);
    }

    @Override
    public GrapeVodPageRequest nextPageRequest() {
        return (GrapeVodPageRequest) super.nextPageRequest();
    }

    @Override
    public GrapeVodPageRequest previousPageRequest() {
        return (GrapeVodPageRequest) super.previousPageRequest();
    }
}
