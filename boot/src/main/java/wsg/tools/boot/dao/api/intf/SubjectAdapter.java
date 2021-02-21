package wsg.tools.boot.dao.api.intf;

import org.apache.http.client.HttpResponseException;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.douban.BaseDoubanSubject;
import wsg.tools.internet.video.site.douban.DoubanSite;

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
     * @throws HttpResponseException if an error occurs
     * @throws NotFoundException     if the subject of the given id is not found from Douban
     */
    BaseDoubanSubject doubanSubject(long dbId) throws HttpResponseException, NotFoundException;

    /**
     * Obtains id of Douban based on id of IMDb.
     *
     * @param imdbId id of IMDb
     * @return result
     * @throws HttpResponseException if an error occurs
     * @throws NotFoundException     if the responding Douban id of the given IMDb id is not found from Douban
     */
    Long getDbIdByImdbId(String imdbId) throws HttpResponseException, NotFoundException;

    /**
     * Obtains subjects of the given user.
     *
     * @param userId id to specify a user
     * @param since  start date
     * @param mark   type of marking
     * @return result
     * @throws HttpResponseException if an error occurs
     * @throws NotFoundException     if subjects of the given user is not found from Douban
     */
    Map<Long, LocalDate> collectUserSubjects(long userId, LocalDate since, MarkEnum mark) throws HttpResponseException, NotFoundException;

    /**
     * Obtains a view of a title from {@link wsg.tools.internet.video.site.imdb.ImdbRepository}.
     *
     * @param imdbId id of IMDb
     * @return result
     * @throws HttpResponseException if an error occurs
     * @throws NotFoundException     if the subject of the given id if not found from IMDb
     */
    ImdbView imdbView(String imdbId) throws HttpResponseException, NotFoundException;
}
