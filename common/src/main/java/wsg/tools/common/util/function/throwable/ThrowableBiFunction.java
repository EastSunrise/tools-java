package wsg.tools.common.util.function.throwable;

/**
 * Represents a function that accepts two arguments, produces a result, and throw an exception. This
 * is the two-arity specialization of {@link ThrowableFunction}.
 *
 * @author Kingen
 * @since 2020/12/3
 */
@FunctionalInterface
public interface ThrowableBiFunction<T, V, R, E extends Throwable> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param v the second function argument
     * @return the function result
     * @throws E thrown exception
     */
    R apply(T t, V v) throws E;
}
