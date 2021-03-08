package wsg.tools.internet.info.adult.midnight;

import java.util.List;
import wsg.tools.internet.base.impl.BasicPageResult;

/**
 * A paged result of {@link MidnightSimpleItem}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class MidnightPageResult extends BasicPageResult<MidnightSimpleItem> {

    MidnightPageResult(List<MidnightSimpleItem> content, MidnightPageRequest pageRequest,
        long total) {
        super(content, pageRequest, total);
    }

    @Override
    public MidnightPageRequest nextPageRequest() {
        return (MidnightPageRequest) super.nextPageRequest();
    }

    @Override
    public MidnightPageRequest previousPageRequest() {
        return (MidnightPageRequest) super.previousPageRequest();
    }
}
