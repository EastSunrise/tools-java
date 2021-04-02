package wsg.tools.internet.movie.resource;

import javax.annotation.Nonnull;
import org.apache.http.client.ResponseHandler;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.HttpSession;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * This class provides a skeletal implementation of a resource site that contains a list repository
 * of items.
 *
 * @param <T> type of items managed by this site
 * @author Kingen
 * @since 2021/3/29
 */
public abstract class AbstractListResourceSite<T> extends BaseSite
    implements RepoRetrievable<Integer, T> {

    AbstractListResourceSite(String name, HttpSession session) {
        super(name, session);
    }

    AbstractListResourceSite(String name, HttpSession session,
        ResponseHandler<String> defaultHandler) {
        super(name, session, defaultHandler);
    }

    /**
     * Constructs a list repository of all items from current site.
     *
     * @return the list repository constructed
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    @Nonnull
    public abstract ListRepository<Integer, T> getRepository() throws OtherResponseException;

    /**
     * Retrieves an item by its identifier.
     *
     * @param id must not be {@literal null}
     * @return the item
     * @throws NullPointerException   if the specified identifier is null
     * @throws NotFoundException      if the item of the specified identifier is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    @Nonnull
    @Override
    public abstract T findById(@Nonnull Integer id)
        throws NotFoundException, OtherResponseException;
}
