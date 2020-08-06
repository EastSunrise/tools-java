package wsg.tools.boot.service.intf;

import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.data.domain.Pageable;
import wsg.tools.boot.pojo.base.PageResult;
import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.dto.QuerySubjectDto;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.result.ImportResult;

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
     * Collect subjects under the user.
     *
     * @param userId user id
     * @param since  since when
     * @return count of collected subjects
     */
    ImportResult importDouban(long userId, LocalDate since);

    /**
     * Import subjects from IMDb with ids.
     *
     * @param ids ids of IMDb
     * @return count of imported subjects
     */
    ImportResult importImdbIds(List<String> ids);

    /**
     * Import subject with doubanId-IMDbId pairs
     *
     * @param pairs pairs of doubanId-IMDbId
     * @return result
     */
    ImportResult importManually(List<MutablePair<Long, String>> pairs);

    /**
     * Import top 250.
     *
     * @return result
     */
    ImportResult top250();

    /**
     * Import weekly movies.
     *
     * @return result
     */
    ImportResult movieWeekly();

    /**
     * Import us box movies.
     *
     * @return result
     */
    ImportResult movieUsBox();

    /**
     * Import movies in theaters.
     *
     * @return result
     */
    ImportResult movieInTheatre();

    /**
     * Import movies coming soon.
     *
     * @return result
     */
    ImportResult movieComingSoon();

    /**
     * Import new movies.
     *
     * @return result
     */
    ImportResult newMovies();

    /**
     * Update subjects
     *
     * @param subjects list of subjects to update whose ids are required
     * @return result
     */
    Result batchUpdate(List<SubjectDto> subjects);

    /**
     * Returns subjects matching the given condition
     *
     * @param querySubjectDto condition
     * @param pageable        pagination, nullable
     * @return page of subjects
     */
    PageResult<SubjectDto> list(QuerySubjectDto querySubjectDto, Pageable pageable);

    /**
     * Obtains ids of not-found subjects.
     *
     * @return list of ids
     */
    List<Object> notFounds();
}
