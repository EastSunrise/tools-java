package wsg.tools.boot.common.util;

import javax.annotation.Nonnull;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.common.util.function.throwable.ThrowableBiFunction;
import wsg.tools.common.util.function.throwable.ThrowableFunction;
import wsg.tools.common.util.function.throwable.ThrowableSupplier;
import wsg.tools.common.util.function.throwable.ThrowableTriFunction;

/**
 * Utility when accessing to {@link wsg.tools.internet}.
 *
 * @author Kingen
 * @since 2021/3/9
 */
public final class SiteUtilExt {

    private static final String NOT_FOUND = "Not found: ";

    private SiteUtilExt() {
    }

    /**
     * Splits the {@link HttpResponseException}.
     *
     * @throws NotFoundException          if not found
     * @throws OtherHttpResponseException other response exceptions
     */
    public static <R> R ifNotFound(ThrowableSupplier<R, HttpResponseException> supplier,
        String message) throws NotFoundException, OtherHttpResponseException {
        try {
            return supplier.get();
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new NotFoundException(NOT_FOUND + message);
            }
            throw new OtherHttpResponseException(e);
        }
    }

    /**
     * Splits the {@link HttpResponseException}.
     *
     * @throws NotFoundException          if not found
     * @throws OtherHttpResponseException other response exceptions
     */
    public static <T, R> R ifNotFound(T t, ThrowableFunction<T, R, HttpResponseException> function,
        @Nonnull String message) throws NotFoundException, OtherHttpResponseException {
        try {
            return function.apply(t);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new NotFoundException(NOT_FOUND + message);
            }
            throw new OtherHttpResponseException(e);
        }
    }

    /**
     * Splits the {@link HttpResponseException}.
     *
     * @throws NotFoundException          if not found
     * @throws OtherHttpResponseException other response exceptions
     */
    public static <T, U, R> R ifNotFound(T t, U u,
        ThrowableBiFunction<T, U, R, HttpResponseException> function, @Nonnull String message)
        throws NotFoundException, OtherHttpResponseException {
        try {
            return function.apply(t, u);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new NotFoundException(NOT_FOUND + message);
            }
            throw new OtherHttpResponseException(e);
        }
    }

    /**
     * Splits the {@link HttpResponseException}.
     *
     * @throws NotFoundException          if not found
     * @throws OtherHttpResponseException other response exceptions
     */
    public static <T, U, V, R> R ifNotFound(T t, U u, V v,
        ThrowableTriFunction<T, U, V, R, HttpResponseException> function, @Nonnull String message)
        throws NotFoundException, OtherHttpResponseException {
        try {
            return function.apply(t, u, v);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new NotFoundException(NOT_FOUND + message);
            }
            throw new OtherHttpResponseException(e);
        }
    }

    /**
     * Splits the {@link HttpResponseException} which should never be not found.
     *
     * @throws OtherHttpResponseException other response exceptions
     */
    public static <R> R found(@Nonnull ThrowableSupplier<R, HttpResponseException> supplier)
        throws OtherHttpResponseException {
        try {
            return supplier.get();
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new AppException(e);
            }
            throw new OtherHttpResponseException(e);
        }
    }

    /**
     * Splits the {@link HttpResponseException} which should never be not found.
     *
     * @throws OtherHttpResponseException other response exceptions
     */
    public static <T, R> R found(@Nonnull T t,
        @Nonnull ThrowableFunction<T, R, HttpResponseException> function)
        throws OtherHttpResponseException {
        try {
            return function.apply(t);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new AppException(e);
            }
            throw new OtherHttpResponseException(e);
        }
    }

    /**
     * Splits the {@link HttpResponseException} which should never be not found.
     *
     * @throws OtherHttpResponseException other response exceptions
     */
    public static <T, U, R> R found(@Nonnull T t, U u,
        @Nonnull ThrowableBiFunction<T, U, R, HttpResponseException> function)
        throws OtherHttpResponseException {
        try {
            return function.apply(t, u);
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new AppException(e);
            }
            throw new OtherHttpResponseException(e);
        }
    }
}
