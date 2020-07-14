package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.BaseQueryDto;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.pojo.enums.StatusEnum;
import wsg.tools.boot.pojo.enums.SubtypeEnum;

/**
 * Condition for querying subjects.
 *
 * @author Kingen
 * @since 2020/6/23
 */
@Getter
@Setter
public class QuerySubjectDto extends BaseQueryDto {

    private StatusEnum status;
    private SubtypeEnum subtype;
    private ArchivedEnum archived;
    private boolean nullDuration;
    private boolean badSeason;
    private boolean nullImdb;
}
