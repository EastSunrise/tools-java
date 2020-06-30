package wsg.tools.boot.service.intf;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import wsg.tools.boot.entity.base.dto.GenericResult;
import wsg.tools.boot.entity.base.dto.Result;
import wsg.tools.boot.entity.subject.dto.SubjectDto;
import wsg.tools.boot.entity.subject.query.QuerySubject;

import java.time.LocalDate;

/**
 * Interface of subject service.
 *
 * @author Kingen
 * @since 2020/6/22
 */
public interface SubjectService extends IService<SubjectDto> {

    /**
     * Obtains list of subjects.
     *
     * @param querySubject condition of query
     * @return page wrapped list of the subjects
     */
    Page<SubjectDto> list(QuerySubject querySubject);

    /**
     * Update info of the subject.
     *
     * @param id id of the subject
     * @return update result
     */
    Result saveOrUpdateInfo(long id);

    /**
     * Play specified subject
     *
     * @param id id of subject
     * @return result
     */
    Result play(long id);

    /**
     * Collect subjects under the user.
     *
     * @param userId    user id
     * @param startDate since when
     * @return result of collecting
     */
    GenericResult<Integer> collectSubjects(long userId, LocalDate startDate);
}
