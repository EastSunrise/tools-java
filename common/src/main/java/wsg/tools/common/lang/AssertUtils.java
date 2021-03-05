package wsg.tools.common.lang;

import java.util.Objects;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility to check objects or conditions before operation
 *
 * @author Kingen
 * @since 2020/6/22
 */
@UtilityClass
public class AssertUtils {

    /**
     * Validate range of an object, [fromInclusive, toExclusive)
     */
    public <T extends Comparable<T>> T requireRange(T target, T fromInclusive, T toExclusive) {
        Objects.requireNonNull(target);
        if (fromInclusive == null && toExclusive == null) {
            return target;
        }
        if (fromInclusive == null) {
            if (target.compareTo(toExclusive) >= 0) {
                throw new IllegalArgumentException("Target must be less than " + toExclusive);
            }
            return target;
        }
        if (toExclusive == null) {
            if (target.compareTo(fromInclusive) < 0) {
                throw new IllegalArgumentException("Target mustn't be less than " + fromInclusive);
            }
            return target;
        }
        if (target.compareTo(fromInclusive) < 0 || target.compareTo(toExclusive) >= 0) {
            throw new IllegalArgumentException(
                "Target must be within range fromInclusive " + fromInclusive + " toExclusive "
                    + toExclusive);
        }
        return target;
    }

    /**
     * Test whether the object equals to another one.
     *
     * @param t1 first object
     * @param t2 another object
     * @throws IllegalArgumentException if test is failed
     */
    public <T> void requireEquals(T t1, T t2) {
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
    public <T> T require(T t, Predicate<? super T> predicate, String message) {
        if (predicate.test(t)) {
            return t;
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * Validate args to be not blank.
     */
    public String requireNotBlank(String arg) {
        if (StringUtils.isBlank(arg)) {
            throw new IllegalArgumentException("Arg mustn't be blank.");
        }
        return arg;
    }

    /**
     * Returns a {@link RuntimeException}.
     */
    public RuntimeException runtimeException(Throwable e) {
        Objects.requireNonNull(e, "Can't construct a RuntimeException from a null exception.");
        return new RuntimeException(e.getMessage(), e);
    }
}
