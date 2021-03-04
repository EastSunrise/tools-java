package wsg.tools.internet.movie.imdb.pojo.object;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import wsg.tools.internet.movie.imdb.pojo.base.BaseImdbObject;

/**
 * Info of persons on IMDb.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("Person")
public class ImdbPerson extends BaseImdbObject {
    private String url;
    private String name;
}
