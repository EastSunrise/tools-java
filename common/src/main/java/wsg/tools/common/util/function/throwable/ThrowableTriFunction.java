package wsg.tools.common.util.function.throwable;

/**
 * Represents a function that accepts three arguments, produces a result, and throw an exception.
 * This is the three-arity specialization of {@link ThrowableFunction}.
 *
 * @author Kingen
 * @since 2020/9/11
 */
@FunctionalInterface
public interface ThrowableTriFunction<T1, T2, T3, R, E extends Throwable> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t1 the first function argument
     * @param t2 the second function argument
     * @param t3 the third function argument
     * @return the function result
     * @throws E thrown exception
     */
    R apply(T1 t1, T2 t2, T3 t3) throws E;
}
