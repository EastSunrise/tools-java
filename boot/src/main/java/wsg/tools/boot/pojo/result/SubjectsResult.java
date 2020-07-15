package wsg.tools.boot.pojo.result;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import wsg.tools.boot.pojo.base.PageResult;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.pojo.enums.MarkEnum;
import wsg.tools.boot.pojo.enums.SubtypeEnum;

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
    private MarkEnum[] statuses = MarkEnum.values();

    public SubjectsResult(Page<SubjectDto> records) {
        super(records);
    }
}
