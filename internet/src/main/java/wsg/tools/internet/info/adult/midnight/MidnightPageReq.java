package wsg.tools.internet.info.adult.midnight;

import javax.annotation.Nonnull;
import wsg.tools.internet.base.page.BasicPageReq;
import wsg.tools.internet.base.page.PageReq;

/**
 * An implementation of {@link PageReq} for {@link MidnightSite}, including a {@link
 * MidnightColumn}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class MidnightPageReq extends BasicPageReq {

    private static final long serialVersionUID = -8443301620276250501L;
    private static final int DEFAULT_SIZE = 30;

    private final MidnightColumn column;

    public MidnightPageReq(int current, @Nonnull MidnightColumn column) {
        super(current, DEFAULT_SIZE);
        this.column = column;
    }

    @Nonnull
    public static MidnightPageReq first(MidnightColumn column) {
        return new MidnightPageReq(0, column);
    }

    @Override
    public MidnightPageReq next() {
        return new MidnightPageReq(super.next().getCurrent(), column);
    }

    @Override
    public MidnightPageReq previous() {
        return new MidnightPageReq(super.previous().getCurrent(), column);
    }

    public MidnightColumn getColumn() {
        return column;
    }
}
