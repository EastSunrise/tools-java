package wsg.tools.boot.dao.api.intf;

import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.enums.MarkEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Adapter for subjects to transfer result of video sites.
 *
 * @author Kingen
 * @since 2020/11/3
 */
public interface SubjectAdapter {

    /**
     * Obtains subject from {@link wsg.tools.internet.video.site.DoubanSite}.
     *
     * @param dbId id of Douban
     * @return result
     */
    GenericResult<BaseDoubanSubject> doubanSubject(long dbId);

    /**
     * Obtains id of Douban based on id of IMDb.
     *
     * @param imdbId id of IMDb
     * @return result
     */
    GenericResult<Long> getDbIdByImdbId(String imdbId);

    /**
     * Obtains subjects of the given user.
     *
     * @param userId id to specify a user
     * @param since  start date
     * @param mark   type of marking
     * @return result
     */
    GenericResult<Map<Long, LocalDate>> collectUserSubjects(long userId, LocalDate since, MarkEnum mark);

    /**
     * Obtains title from {@link wsg.tools.internet.video.site.ImdbSite}.
     *
     * @param imdbId id of IMDb
     * @return result
     */
    GenericResult<BaseImdbTitle> imdbTitle(String imdbId);

    /**
     * Obtains all episodes of the given series,
     *
     * @param seriesId id to specify series.
     * @return result
     */
    GenericResult<List<String[]>> episodes(String seriesId);
}
