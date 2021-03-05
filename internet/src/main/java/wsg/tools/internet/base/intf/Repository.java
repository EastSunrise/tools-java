package wsg.tools.internet.base.intf;

import javax.annotation.Nonnull;
import org.apache.http.client.HttpResponseException;

/**
 * A repository which contains a group of records with certain rules, similar to a table in the
 * database.
 *
 * @param <T> type of the record which needs include the corresponding {@code ID}.
 * @author Kingen
 * @since 2021/1/12
 */
@FunctionalInterface
public interface Repository<ID, T> {

    /**
     * Obtains a record by the given identifier.
     *
     * @param id identifier of the item
     * @return the record
     * @throws HttpResponseException if an error occurs
     */
    T findById(@Nonnull ID id) throws HttpResponseException;
}
