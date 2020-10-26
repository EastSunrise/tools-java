package wsg.tools.common.util.function.throwable;

/**
 * Represents an operation that accepts a single input argument, throws an
 * exception, and returns no result.
 *
 * @param <T> the type of the input to the operation
 * @param <E> the type of thrown exception
 * @author Kingen
 * @since 2020/10/23
 */
@FunctionalInterface
public interface ThrowableConsumer<T, E extends Throwable> {


    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     * @throws E thrown exception
     */
    void accept(T t) throws E;
}
