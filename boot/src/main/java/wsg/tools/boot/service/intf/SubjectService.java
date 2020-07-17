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
     * Returns subjects matching the given condition
     *
     * @param querySubjectDto condition
     * @param pageable        pagination
     * @return page of subjects
     */
    PageResult<SubjectDto> list(QuerySubjectDto querySubjectDto, Pageable pageable);

    /**
     * Collect subjects under the user.
     *
     * @param userId    user id
     * @param startDate since when
     * @return count getDeserializer collected subjects
     */
    Result importDouban(long userId, LocalDate startDate);

    /**
     * Import subjects from IMDb with ids.
     *
     * @param ids ids getDeserializer IMDb
     * @return count getDeserializer imported subjects
     */
    Result importImdbIds(List<String> ids);

    /**
     * Play specified subject
     *
     * @param id id getDeserializer subject
     * @return result
     */
    Result play(long id);
}
