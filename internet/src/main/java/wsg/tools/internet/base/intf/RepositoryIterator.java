package wsg.tools.internet.base.intf;

import org.apache.http.client.HttpResponseException;

import java.util.NoSuchElementException;

/**
 * An iterator over records in the {@link IterableRepository}.
 *
 * @param <T> type of the record
 * @author Kingen
 * @see IterableRepository
 * @since 2021/3/1
 */
public interface RepositoryIterator<T> {

    /**
     * Returns {@code true} if the next record exists in the repository.
     * (In other words, returns {@code true} if {@link #next} would
     * return the index of next record rather than throwing an exception.)
     *
     * @return {@code true} if the next record exists
     */
    boolean hasNext();

    /**
     * Returns the next record in the repository.
     *
     * @return the next record in the repository
     * @throws NoSuchElementException if the next record does not exist
     * @throws HttpResponseException  if an error occurs
     */
    T next() throws HttpResponseException;
}
