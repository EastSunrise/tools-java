package wsg.tools.boot.dao.jpa.converter;

import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.internet.video.enums.MarkEnum;

import java.util.function.Supplier;

/**
 * Adapter between a code and a {@code MarkEnum}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
enum MarkEnumAdapter implements IntCodeSupplier, Supplier<MarkEnum>, AkaPredicate<MarkEnum> {
    WISH(1, MarkEnum.WISH),
    DO(2, MarkEnum.DO),
    COLLECT(3, MarkEnum.COLLECT),
    ;

    private final int code;
    private final MarkEnum mark;

    MarkEnumAdapter(int code, MarkEnum mark) {
        this.code = code;
        this.mark = mark;
    }

    @Override
    public MarkEnum get() {
        return mark;
    }

    @Override
    public boolean alsoKnownAs(MarkEnum other) {
        return mark == other;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
