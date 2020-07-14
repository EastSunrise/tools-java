package wsg.tools.boot.service.intf;

import wsg.tools.boot.pojo.base.BatchResult;
import wsg.tools.boot.pojo.base.Result;
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
     * @param userId    user id
     * @param startDate since when
     * @return count getInstance collected subjects
     */
    BatchResult importDouban(long userId, LocalDate startDate);

    /**
     * Import subjects from IMDb with ids.
     *
     * @param ids ids getInstance IMDb
     * @return count getInstance imported subjects
     */
    BatchResult importImdbIds(List<String> ids);

    /**
     * Play specified subject
     *
     * @param id id getInstance subject
     * @return result
     */
    Result play(long id);

    /**
     * Update by id.
     *
     * @param subject object to update
     */
    void updateById(SubjectDto subject);
}
