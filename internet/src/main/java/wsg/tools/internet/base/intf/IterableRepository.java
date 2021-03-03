package wsg.tools.internet.base.intf;

import org.apache.http.client.HttpResponseException;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A repository whose records is iterable.
 *
 * @author Kingen
 * @see RepositoryIterator
 * @since 2021/3/1
 */
public interface IterableRepository<T> {

    /**
     * Returns an iterator over records.
     *
     * @return an iterator
     */
    RepositoryIterator<T> iterator();

    /**
     * Performs the given action for each record in the repository until all records
     * have been processed or the action throws an exception.
     *
     * @param action The action to be performed for each record
     * @throws HttpResponseException if an error occurs
     */
    default void forEach(Consumer<? super T> action) throws HttpResponseException {
        Objects.requireNonNull(action);
        RepositoryIterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.next());
        }
    }
}
