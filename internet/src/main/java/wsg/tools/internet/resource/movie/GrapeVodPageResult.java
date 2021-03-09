package wsg.tools.internet.resource.movie;

import java.util.List;
import wsg.tools.internet.base.impl.BasicPageResult;

/**
 * A paged result of {@link GrapeVodSimpleItem}.
 *
 * @author Kingen
 * @since 2021/3/9
 */
public class GrapeVodPageResult extends BasicPageResult<GrapeVodSimpleItem> {

    GrapeVodPageResult(List<GrapeVodSimpleItem> content, GrapeVodPageRequest request, long total) {
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
