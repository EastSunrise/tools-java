package wsg.tools.internet.movie.imdb.pojo.base;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import wsg.tools.internet.movie.imdb.pojo.object.ImdbOrganization;
import wsg.tools.internet.movie.imdb.pojo.object.ImdbPerson;

/**
 * Base object from IMDb.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({@JsonSubTypes.Type(value = ImdbPerson.class, name = "Person"),
    @JsonSubTypes.Type(value = ImdbOrganization.class, name = "Organization"),})
public abstract class BaseImdbObject {

}
