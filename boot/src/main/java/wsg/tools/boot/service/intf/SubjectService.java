package wsg.tools.boot.service.intf;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.boot.pojo.base.ListResult;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.pojo.result.BatchResult;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface of subject service.
 *
 * @author Kingen
 * @since 2020/6/22
 */
public interface SubjectService {

    /**
     * Insert entity obtained by id of douban.
     *
     * @param dbId id of Douban, not null
     * @return result with subject id
     * @throws IOException unexpected IOException except HttpResponseException
     */
    GenericResult<Long> insertSubjectByDb(long dbId) throws IOException;

    /**
     * Insert entity obtained by id of IMDb.
     *
     * @param imdbId id of IMDb, not null
     * @param dbId   may null
     * @return result with subject id
     * @throws IOException unexpected IOException except HttpResponseException
     */
    GenericResult<Long> insertSubjectByImdb(String imdbId, Long dbId) throws IOException;

    /**
     * Collect subjects under the user.
     *
     * @param userId user id
     * @param since  since when
     * @return result of importing
     */
    BatchResult<Long> importDouban(long userId, LocalDate since);

    /**
     * Import subject with doubanId-IMDbId pairs
     *
     * @param ids key-values of doubanId-IMDbId
     * @return result
     */
    BatchResult<String> importManually(List<DefaultKeyValue<String, Long>> ids);

    /**
     * Returns subjects matching the given condition
     *
     * @return list of subjects
     */
    ListResult<SubjectEntity> export();
}
