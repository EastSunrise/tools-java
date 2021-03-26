package wsg.tools.common.util.function;

/**
 * Represents a function that accepts one argument, produces a result, and throw two exceptions.
 * This is an extension of {@link org.apache.commons.lang3.Functions.FailableFunction}.
 *
 * @author Kingen
 * @since 2021/3/26
 */
public interface BiThrowableFunction<T, R, E extends Throwable, F extends Throwable> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the argument of the function
     * @return the function result
     * @throws E the first thrown exception
     * @throws F the second thrown exception
     */
    R apply(T t) throws E, F;
}
