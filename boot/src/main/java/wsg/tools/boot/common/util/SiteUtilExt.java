package wsg.tools.boot.common.util;

import javax.annotation.Nonnull;
import org.apache.commons.lang3.Functions;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.common.util.function.ThrowableTriFunction;

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
    public static <R> R ifNotFound(Functions.FailableSupplier<R, HttpResponseException> supplier,
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
    public static <T, R> R ifNotFound(T t,
        Functions.FailableFunction<T, R, HttpResponseException> function,
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
        Functions.FailableBiFunction<T, U, R, HttpResponseException> function,
        @Nonnull String message)
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
    public static <R> R found(
        @Nonnull Functions.FailableSupplier<R, HttpResponseException> supplier)
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
        @Nonnull Functions.FailableFunction<T, R, HttpResponseException> function)
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
        @Nonnull Functions.FailableBiFunction<T, U, R, HttpResponseException> function)
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
