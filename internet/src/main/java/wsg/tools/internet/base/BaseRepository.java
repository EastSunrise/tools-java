package wsg.tools.internet.base;

import org.apache.http.client.HttpResponseException;

import java.util.List;

/**
 * Interface for generic CRUD operations on a site for its items.
 *
 * @param <T> type of the record which needs include the corresponding {@code ID}.
 * @author Kingen
 * @since 2021/1/12
 */
public interface BaseRepository<ID, T> {

    /**
     * Obtains all available items on the site.
     *
     * @return all items
     * @throws HttpResponseException if an error occurs
     */
    List<T> findAll() throws HttpResponseException;

    /**
     * Obtains an item by the given identifier.
     *
     * @param id identifier of the item
     * @return item
     * @throws HttpResponseException if an error occurs
     */
    T findById(ID id) throws HttpResponseException;
}
