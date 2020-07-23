package wsg.tools.common.function;

/**
 * Indicate what the object is also known as.
 *
 * @author Kingen
 * @since 2020/6/19
 */
@FunctionalInterface
public interface AkaPredicate<Aka> {
    /**
     * Also known as, similar to {@link #equals(Object)}
     *
     * @param other aka
     * @return if this is also known as {@code other}
     */
    boolean alsoKnownAs(Aka other);
}
