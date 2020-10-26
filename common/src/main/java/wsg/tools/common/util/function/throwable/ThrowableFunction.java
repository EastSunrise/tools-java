package wsg.tools.common.util.function.throwable;

/**
 * Represents a function that accepts one argument, produces a result, and throw an exception.
 *
 * @author Kingen
 * @since 2020/9/11
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Throwable> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws E thrown exception
     */
    R apply(T t) throws E;
}
