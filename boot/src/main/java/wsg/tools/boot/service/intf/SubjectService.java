package wsg.tools.boot.service.intf;

import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.springframework.data.domain.Pageable;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.boot.pojo.base.PageResult;
import wsg.tools.boot.pojo.dto.QuerySubjectDto;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.result.ImportResult;

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
    ImportResult importDouban(long userId, LocalDate since);

    /**
     * Import subject with doubanId-IMDbId pairs
     *
     * @param ids key-values of doubanId-IMDbId
     * @return result
     */
    ImportResult importManually(List<DefaultKeyValue<String, Long>> ids);

    /**
     * Returns subjects matching the given condition
     *
     * @param querySubjectDto condition
     * @param pageable        pagination, nullable
     * @return page of subjects
     */
    PageResult<SubjectDto> list(QuerySubjectDto querySubjectDto, Pageable pageable);
}
