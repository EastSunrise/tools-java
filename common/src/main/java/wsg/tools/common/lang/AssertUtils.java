package wsg.tools.common.lang;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Utility to check objects or conditions before operation
 *
 * @author Kingen
 * @since 2020/6/22
 */
public final class AssertUtils {

    /**
     * Validate range of an object, [fromInclusive, toExclusive)
     */
    public static <T extends Comparable<T>> void requireRange(T target, T fromInclusive, T toExclusive) {
        Objects.requireNonNull(target);
        if (fromInclusive == null && toExclusive == null) {
            return;
        }
        if (fromInclusive == null) {
            if (target.compareTo(toExclusive) >= 0) {
                throw new IllegalArgumentException("Target must be less than " + toExclusive);
            }
            return;
        }
        if (toExclusive == null) {
            if (target.compareTo(fromInclusive) < 0) {
                throw new IllegalArgumentException("Target mustn't be less than " + fromInclusive);
            }
            return;
        }
        if (target.compareTo(fromInclusive) < 0 || target.compareTo(toExclusive) >= 0) {
            throw new IllegalArgumentException("Target must be within range fromInclusive " + fromInclusive + " toExclusive " + toExclusive);
        }
    }

    /**
     * Test whether the object equals to another one.
     *
     * @param t1 first object
     * @param t2 another object
     * @throws IllegalArgumentException if test is failed
     */
    public static <T> void requireEquals(T t1, T t2) {
        if (!Objects.equals(t1, t2)) {
            throw new IllegalArgumentException("'" + t1 + "' doesn't equal to '" + t2 + "'");
        }
    }

    /**
     * Test an object.
     *
     * @param t         given object
     * @param predicate function to test the object
     * @return the object
     * @throws IllegalArgumentException if test is failed
     */
    public static <T> T test(T t, Predicate<T> predicate, String message) {
        if (predicate.test(t)) {
            return t;
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * Validate args to be not blank.
     */
    public static void requireNotBlank(String arg) {
        if (StringUtils.isBlank(arg)) {
            throw new IllegalArgumentException("Arg mustn't be blank.");
        }
    }

    /**
     * Returns a {@link RuntimeException}.
     */
    public static RuntimeException runtimeException(Throwable e) {
        Objects.requireNonNull(e, "Can't construct a RuntimeException from a null exception.");
        return new RuntimeException(e.getMessage(), e);
    }
}
