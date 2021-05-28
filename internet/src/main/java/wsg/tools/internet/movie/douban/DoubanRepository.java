package wsg.tools.internet.movie.douban;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import wsg.tools.internet.base.Loggable;
import wsg.tools.internet.base.page.Page;
import wsg.tools.internet.base.page.PageIndex;
import wsg.tools.internet.common.LoginException;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.movie.common.enums.DoubanMark;

/**
 * The interface provides methods to query subjects on Douban.
 * <p>
 * Notes that some x-rated subjects may be restricted to access without logging in.
 *
 * @author Kingen
 * @see <a href="https://www.douban.com/">Douban</a>
 * @since 2021/5/11
 */
public interface DoubanRepository extends Loggable<Long> {

    /**
     * Logs in the site with the specified username and password.
     *
     * @param username the username of the account
     * @param password the password of the account
     * @throws OtherResponseException if an unexpected error occurs
     * @throws LoginException         if the specified user and password is invalid or a CAPTCHA is
     *                                required.
     */
    void login(String username, String password)
        throws OtherResponseException, LoginException;

    /**
     * Logs out if logged-in.
     *
     * @throws OtherResponseException if an unexpected error occurs
     */
    void logout() throws OtherResponseException;

    /**
     * Searches subjects related to the specified keyword globally.
     * <p>
     * This method is limited to 10 times once.
     *
     * @param keyword keyword to search
     * @param page    pagination information
     * @param catalog the catalog to filter subjects, may null
     * @return searched subjects in page
     * @throws OtherResponseException   if an unexpected error occurs
     * @throws IllegalArgumentException if the keyword is blank
     */
    @Nonnull
    Page<SubjectIndex> searchGlobally(String keyword, @Nonnull PageIndex page,
        @Nullable DoubanCatalog catalog) throws OtherResponseException;

    /**
     * Searches subjects related to the specified keyword under the specified catalog module.
     *
     * @param catalog the catalog to which the subjects belong
     * @param keyword keyword to search
     * @return indices of searched subjects
     * @throws OtherResponseException   if an unexpected error occurs
     * @throws IllegalArgumentException if the keyword is blank
     */
    @Nonnull
    List<SubjectIndex> search(@Nonnull DoubanCatalog catalog, String keyword)
        throws OtherResponseException;

    /**
     * Retrieves marked subjects of the specified user.
     *
     * @param catalog catalog of the subjects to be queried
     * @param userId  the id of the user to be queried
     * @param mark    type of marking
     * @param page    pagination information
     * @return marked subjects in page
     * @throws NotFoundException      if the user is not found
     * @throws OtherResponseException if an unexpected error occurs
     */
    @Nonnull
    Page<MarkedSubject> findUserSubjects(@Nonnull DoubanCatalog catalog, long userId,
        @Nonnull DoubanMark mark, @Nonnull PageIndex page)
        throws NotFoundException, OtherResponseException;

    /**
     * Retrieves collected creators of the specified user.
     *
     * @param userId  the id of the user to be queried
     * @param catalog catalog of the creators to be queried
     * @param page    pagination information
     * @return collected creators in page
     * @throws NotFoundException      if the user is not found
     * @throws OtherResponseException if an unexpected error occurs
     */
    @Nonnull
    Page<PersonIndex> findUserCreators(@Nonnull DoubanCatalog catalog, long userId,
        @Nonnull PageIndex page) throws NotFoundException, OtherResponseException;

    /**
     * Retrieves the movie of the specified id.
     *
     * @param id id of the movie to be queried
     * @return the movie
     * @throws NotFoundException      if the user is not found
     * @throws OtherResponseException if an unexpected error occurs
     */
    AbstractMovie findMovieById(long id) throws NotFoundException, OtherResponseException;

    /**
     * Retrieves the book of the specified id.
     *
     * @param id id of the book to be queried
     * @return the book
     * @throws NotFoundException      if the user is not found
     * @throws OtherResponseException if an unexpected error occurs
     */
    DoubanBook findBookById(long id) throws NotFoundException, OtherResponseException;

    /**
     * Gets the corresponding subject-id of the specified IMDb-id.
     *
     * @param imdbId the id of the subject on IMDb
     * @return the corresponding id of the subject
     * @throws NotFoundException      if the specified IMDb-id is not found
     * @throws OtherResponseException if an unexpected error occurs
     * @throws LoginException         if not logged-in
     */
    long getDbIdByImdbId(String imdbId)
        throws NotFoundException, OtherResponseException, LoginException;
}
