package wsg.tools.internet.base.repository;

import javax.annotation.Nonnull;
import wsg.tools.internet.base.page.PageRequest;
import wsg.tools.internet.base.page.PageResult;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * A repository whose identifiers are pageable.
 *
 * @author Kingen
 * @since 2021/3/26
 */
public interface PageRepository<ID, T>
    extends Repository<ID, T>, RepoIterable<T> {

    /**
     * Retrieves a paged result of the identifiers in this repository.
     *
     * @param request the page request
     * @return a paged result of the identifiers
     * @throws NullPointerException   if the specified request is null
     * @throws NotFoundException      if the page of the specified request is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    @Nonnull
    <P extends PageRequest, R extends PageResult<ID>> R findPageResult(P request)
        throws NotFoundException, OtherResponseException;
}
