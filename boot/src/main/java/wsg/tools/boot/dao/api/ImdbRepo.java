package wsg.tools.boot.dao.api;

import java.io.Closeable;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * The repository that retrieves subjects from Internet Movie Database.
 *
 * @author Kingen
 * @since 2021/5/21
 */
public interface ImdbRepo extends Closeable {

    /**
     * Retrieves the subject of the specified id.
     *
     * @param imdbId the id of the movie
     * @return view of the subject
     * @throws NotFoundException      if the subject is not found
     * @throws OtherResponseException if an unexpected error occurs
     */
    ImdbView findSubjectById(String imdbId) throws NotFoundException, OtherResponseException;
}
