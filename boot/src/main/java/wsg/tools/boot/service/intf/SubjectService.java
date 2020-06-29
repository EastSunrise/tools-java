package wsg.tools.boot.service.intf;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import wsg.tools.boot.entity.base.Result;
import wsg.tools.boot.entity.subject.dto.SubjectDto;
import wsg.tools.boot.entity.subject.query.QuerySubject;

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
    Result updateInfo(long id);

    /**
     * Play specified subject
     *
     * @param id id of subject
     * @return result
     */
    Result play(long id);
}
