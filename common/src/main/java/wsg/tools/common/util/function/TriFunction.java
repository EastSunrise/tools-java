package wsg.tools.common.util.function;

import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result. This is the three-arity
 * specialization of {@link Function}.
 *
 * @author Kingen
 * @since 2020/9/11
 */
@FunctionalInterface
public interface TriFunction<T1, T2, T3, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t1 the first function argument
     * @param t2 the second function argument
     * @param t3 the third function argument
     * @return the function result
     */
    R apply(T1 t1, T2 t2, T3 t3);
}
