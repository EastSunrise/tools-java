package wsg.tools.internet.base.repository;

import javax.annotation.Nonnull;
import wsg.tools.internet.base.page.PageReq;
import wsg.tools.internet.base.page.PageResult;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * Indicates that the repository is pageable, including a method to retrieve a paged result of
 * indices by given pagination information.
 *
 * @param <P> type of pagination information
 * @param <R> type of paged result returned by the repository
 * @author Kingen
 * @since 2021/3/28
 */
public interface RepoPageable<P extends PageReq, R extends PageResult<?, P>> {

    /**
     * Retrieves a paged result of the indices by the given pagination information.
     *
     * @param request the pagination information
     * @return a paged result of the indices
     * @throws NullPointerException   if the specified request is null
     * @throws NotFoundException      if the page of the specified request is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    @Nonnull
    R findPage(P request) throws NotFoundException, OtherResponseException;
}
