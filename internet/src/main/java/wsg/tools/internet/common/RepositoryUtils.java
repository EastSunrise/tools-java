package wsg.tools.internet.common;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.RepositoryIterator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for repositories.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public final class RepositoryUtils {

    public static <T> List<T> findAll(@Nonnull IterableRepository<T> repository) throws HttpResponseException {
        RepositoryIterator<T> iterator = repository.iterator();
        List<T> all = new ArrayList<>();
        while (iterator.hasNext()) {
            T t = iterator.next();
            all.add(t);
        }
        return all;
    }

    public static <T> List<T> findAllIgnoreNotfound(@Nonnull IterableRepository<T> repository) throws HttpResponseException {
        RepositoryIterator<T> iterator = repository.iterator();
        List<T> all = new ArrayList<>();
        while (iterator.hasNext()) {
            try {
                all.add(iterator.next());
            } catch (HttpResponseException e) {
                if (e.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                    throw e;
                }
            }
        }
        return all;
    }
}
