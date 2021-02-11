package wsg.tools.boot.dao.api.intf;

import wsg.tools.boot.pojo.error.SiteException;
import wsg.tools.boot.pojo.result.SingleResult;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.douban.BaseDoubanSubject;
import wsg.tools.internet.video.site.douban.DoubanSite;
import wsg.tools.internet.video.site.imdb.ImdbSite;
import wsg.tools.internet.video.site.imdb.ImdbTitle;

import java.time.LocalDate;
import java.util.Map;

/**
 * Adapter for subjects to transfer result of video sites.
 *
 * @author Kingen
 * @since 2020/11/3
 */
public interface SubjectAdapter {

    /**
     * Obtains subject from {@link DoubanSite}.
     *
     * @param dbId id of Douban
     * @return result
     * @throws NotFoundException if not found
     */
    SingleResult<BaseDoubanSubject> doubanSubject(long dbId) throws NotFoundException;

    /**
     * Obtains id of Douban based on id of IMDb.
     *
     * @param imdbId id of IMDb
     * @return result
     * @throws SiteException     if server error occurs when getting access to the site
     * @throws NotFoundException if not found
     */
    SingleResult<Long> getDbIdByImdbId(String imdbId) throws SiteException, NotFoundException;

    /**
     * Obtains subjects of the given user.
     *
     * @param userId id to specify a user
     * @param since  start date
     * @param mark   type of marking
     * @return result
     * @throws NotFoundException if not found
     */
    SingleResult<Map<Long, LocalDate>> collectUserSubjects(long userId, LocalDate since, MarkEnum mark) throws NotFoundException;

    /**
     * Obtains title from {@link ImdbSite}.
     *
     * @param imdbId id of IMDb
     * @return result
     * @throws NotFoundException if not found
     */
    SingleResult<ImdbTitle> imdbTitle(String imdbId) throws NotFoundException;
}
