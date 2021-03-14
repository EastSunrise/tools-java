package wsg.tools.common.util.function;

/**
 * Represents a function that accepts three arguments, produces a result, and throw an exception.
 * This is the three-arity specialization of {@link org.apache.commons.lang3.Functions.FailableFunction}.
 *
 * @author Kingen
 * @since 2020/9/11
 */
@FunctionalInterface
public interface ThrowableTriFunction<T, U, V, R, E extends Throwable> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param v the third function argument
     * @return the function result
     * @throws E thrown exception
     */
    R apply(T t, U u, V v) throws E;
}
