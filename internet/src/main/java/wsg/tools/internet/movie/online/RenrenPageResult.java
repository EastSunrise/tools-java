package wsg.tools.internet.movie.online;

import java.util.List;
import wsg.tools.internet.base.page.AbstractPage;
import wsg.tools.internet.base.page.PageIndex;

/**
 * A paged result of {@code RenrenMovieIndex}.
 *
 * @author Kingen
 * @since 2021/3/22
 */
public class RenrenPageResult extends AbstractPage<RenrenSeriesIndex> {

    private static final long serialVersionUID = 7967804881652489036L;

    private final boolean end;

    RenrenPageResult(List<RenrenSeriesIndex> content, PageIndex pageIndex, boolean end) {
        super(content, pageIndex, 10);
        this.end = end;
    }

    @Override
    public boolean hasNext() {
        return !end;
    }
}
