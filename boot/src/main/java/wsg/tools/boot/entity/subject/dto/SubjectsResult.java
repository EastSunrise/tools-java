package wsg.tools.boot.entity.subject.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.entity.base.dto.PageResult;
import wsg.tools.boot.entity.subject.enums.ArchivedEnum;
import wsg.tools.boot.entity.subject.enums.StatusEnum;
import wsg.tools.boot.entity.subject.enums.SubtypeEnum;

/**
 * Result of subjects.
 *
 * @author Kingen
 * @since 2020/7/10
 */
@Getter
@Setter
public class SubjectsResult extends PageResult<SubjectDto> {
    private SubtypeEnum[] subtypes = SubtypeEnum.values();
    private ArchivedEnum[] archivedEnums = ArchivedEnum.values();
    private StatusEnum[] statuses = StatusEnum.values();

    public SubjectsResult(Page<SubjectDto> page) {
        super(page);
    }
}
