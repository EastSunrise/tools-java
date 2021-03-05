package wsg.tools.internet.base.intf;

import java.util.List;
import org.apache.http.client.HttpResponseException;

/**
 * A repository with limited records which has stopped updating.
 *
 * @author Kingen
 * @since 2021/3/1
 */
@FunctionalInterface
public interface LimitedRepository<ID, T> {

    /**
     * Obtains all records in the repository.
     *
     * @return list of all records
     * @throws HttpResponseException if an error occurs
     */
    List<T> findAll() throws HttpResponseException;
}
