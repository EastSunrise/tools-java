package wsg.tools.internet.video.entity.imdb.base;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.object.ImdbOrganization;
import wsg.tools.internet.video.entity.imdb.object.ImdbPerson;

/**
 * Base object from IMDb.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImdbPerson.class, name = "Person"),
        @JsonSubTypes.Type(value = ImdbOrganization.class, name = "Organization"),
})
public abstract class BaseImdbObject {
}
