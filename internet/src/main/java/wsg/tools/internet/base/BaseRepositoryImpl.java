package wsg.tools.internet.base;

import org.apache.http.client.ResponseHandler;
import wsg.tools.internet.common.Scheme;

/**
 * Base Implementation of {@link BaseRepository}.
 *
 * @author Kingen
 * @since 2021/2/28
 */
public abstract class BaseRepositoryImpl<ID, T> extends RepositoryImpl implements BaseRepository<ID, T> {

    protected BaseRepositoryImpl(String name, String domain) {
        super(name, domain);
    }

    protected BaseRepositoryImpl(String name, Scheme scheme, String domain) {
        super(name, scheme, domain);
    }

    protected BaseRepositoryImpl(String name, String domain, ResponseHandler<String> handler) {
        super(name, domain, handler);
    }

    protected BaseRepositoryImpl(String name, Scheme scheme, String domain, ResponseHandler<String> handler) {
        super(name, scheme, domain, handler);
    }
}
