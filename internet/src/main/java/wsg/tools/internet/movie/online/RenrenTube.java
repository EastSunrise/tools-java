package wsg.tools.internet.movie.online;

import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.page.PageIndex;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * The interface of Renren Tube.
 *
 * @author Kingen
 * @see <a href="http://m.rr.tv/">Renren Tube</a>
 * @since 2021/5/31
 */
public interface RenrenTube extends RepoRetrievable<Integer, RenrenSeries> {

    /**
     * Retrieves series by page.
     *
     * @param req       the request with optional arguments
     * @param pageIndex pagination index
     * @return series in page
     * @throws NotFoundException      if the target page is not found
     * @throws OtherResponseException if an unexpected error occurs
     */
    RenrenPageResult findAll(@Nonnull RenrenReq req, PageIndex pageIndex)
        throws NotFoundException, OtherResponseException;

    /**
     * Searches items by the specified keyword.
     *
     * @param keyword the keyword to search
     * @param size    the size of returned items, or 3 if the specified one is illegal
     * @return list of searched items
     * @throws OtherResponseException if an unexpected error occurs
     */
    List<SearchedItem> search(String keyword, int size) throws OtherResponseException;
}
