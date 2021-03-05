package wsg.tools.boot.dao.jpa.converter;

import java.util.function.Supplier;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.internet.movie.common.enums.DoubanMark;

/**
 * Adapter between a code and a {@code MarkEnum}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
enum MarkEnumAdapter implements IntCodeSupplier, Supplier<DoubanMark>, AkaPredicate<DoubanMark> {
    /**
     * wish/do/collect
     */
    WISH(1, DoubanMark.WISH), DO(2, DoubanMark.DO), COLLECT(3, DoubanMark.COLLECT),
    ;

    private final int code;
    private final DoubanMark mark;

    MarkEnumAdapter(int code, DoubanMark mark) {
        this.code = code;
        this.mark = mark;
    }

    @Override
    public DoubanMark get() {
        return mark;
    }

    @Override
    public boolean alsoKnownAs(DoubanMark other) {
        return mark == other;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
