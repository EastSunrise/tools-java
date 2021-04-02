package wsg.tools.internet.resource.online;

import java.util.List;
import wsg.tools.internet.base.page.AbstractPageResult;

/**
 * A paged result of {@code RenrenMovieIndex}.
 *
 * @author Kingen
 * @since 2021/3/22
 */
public class RenrenPageResult extends AbstractPageResult<RenrenSeriesIndex, RenrenPageReq> {

    private final boolean end;

    RenrenPageResult(List<RenrenSeriesIndex> content, RenrenPageReq request, boolean end) {
        super(content, request);
        this.end = end;
    }

    @Override
    public boolean hasNext() {
        return !end;
    }
}
