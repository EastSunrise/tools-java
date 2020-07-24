package wsg.tools.boot.service.intf;

import org.springframework.data.domain.Pageable;
import wsg.tools.boot.pojo.base.PageResult;
import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.dto.QuerySubjectDto;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.service.base.BaseService;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface of subject service.
 *
 * @author Kingen
 * @since 2020/6/22
 */
public interface SubjectService extends BaseService<SubjectDto, SubjectEntity, Long> {

    /**
     * Collect subjects under the user.
     *
     * @param userId user id
     * @param since  since when
     * @return count of collected subjects
     */
    Result importDouban(long userId, LocalDate since);

    /**
     * Import subjects from IMDb with ids.
     *
     * @param ids ids of IMDb
     * @return count of imported subjects
     */
    Result importImdbIds(List<String> ids);

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
     * Play specified subject
     *
     * @param id id of subject
     * @return result
     */
    Result play(long id);
}
