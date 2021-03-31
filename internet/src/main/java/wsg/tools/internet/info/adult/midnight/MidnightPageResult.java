package wsg.tools.internet.info.adult.midnight;

import java.util.List;
import wsg.tools.internet.base.page.AmountCountablePageResult;

/**
 * A paged result of {@link MidnightIndex}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class MidnightPageResult extends AmountCountablePageResult<MidnightIndex, MidnightPageReq> {

    MidnightPageResult(List<MidnightIndex> content, MidnightPageReq request, long total) {
        super(content, request, total);
    }
}
