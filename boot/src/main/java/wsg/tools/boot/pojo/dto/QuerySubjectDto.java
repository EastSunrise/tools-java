package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.BaseQueryDto;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.pojo.enums.TypeEnum;
import wsg.tools.internet.video.enums.MarkEnum;

/**
 * Condition for querying subjects.
 *
 * @author Kingen
 * @since 2020/6/23
 */
@Getter
@Setter
public class QuerySubjectDto extends BaseQueryDto {

    private MarkEnum mark;
    private ArchivedEnum archived;
    private TypeEnum type;
    private Boolean incomplete = false;
}
