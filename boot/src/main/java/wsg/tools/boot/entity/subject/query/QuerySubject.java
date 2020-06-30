package wsg.tools.boot.entity.subject.query;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.entity.base.dto.BaseQuery;
import wsg.tools.boot.entity.subject.enums.ArchivedEnum;
import wsg.tools.boot.entity.subject.enums.StatusEnum;
import wsg.tools.boot.entity.subject.enums.SubtypeEnum;

/**
 * Condition for querying subjects.
 *
 * @author Kingen
 * @since 2020/6/23
 */
@Getter
@Setter
public class QuerySubject extends BaseQuery {

    private StatusEnum status;
    private SubtypeEnum subtype;
    private ArchivedEnum archived;
    private boolean nullDuration;
    private boolean badSeason;
    private boolean nullImdb;
}
