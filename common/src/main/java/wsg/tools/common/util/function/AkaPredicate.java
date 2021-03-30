package wsg.tools.common.util.function;

/**
 * Represents a predicate (boolean-valued function) of that the object is also known as.
 *
 * @author Kingen
 * @since 2020/6/19
 */
@FunctionalInterface
public interface AkaPredicate<T> {

    /**
     * Also known as, similar to {@link #equals(Object)}
     *
     * @param other aka
     * @return if this is also known as {@code other}
     */
    boolean alsoKnownAs(T other);
}
