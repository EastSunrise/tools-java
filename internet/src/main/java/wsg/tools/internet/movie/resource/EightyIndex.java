package wsg.tools.internet.movie.resource;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.common.lang.AssertUtils;

/**
 * The index of an item on {@link EightyMovieSite}.
 *
 * @author Kingen
 * @since 2021/6/15
 */
public class EightyIndex {

    private final EightyType type;
    private final int id;

    public EightyIndex(EightyType type, int id) {
        this.type = Objects.requireNonNull(type);
        this.id = AssertUtils.requireRange(id, 1, null);
    }

    @Nonnull
    @Contract("_ -> new")
    public static EightyIndex movie(int id) {
        return new EightyIndex(EightyType.MOVIE, id);
    }

    @Nonnull
    @Contract("_ -> new")
    public static EightyIndex tv(int id) {
        return new EightyIndex(EightyType.TV, id);
    }

    @Nonnull
    @Contract("_ -> new")
    public static EightyIndex anime(int id) {
        return new EightyIndex(EightyType.CARTOON, id);
    }

    @Nonnull
    @Contract("_ -> new")
    public static EightyIndex variety(int id) {
        return new EightyIndex(EightyType.VARIETY, id);
    }

    public EightyType getType() {
        return type;
    }

    public int getId() {
        return id;
    }
}
