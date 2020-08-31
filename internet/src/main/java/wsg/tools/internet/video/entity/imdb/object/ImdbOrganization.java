package wsg.tools.internet.video.entity.imdb.object;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbObject;

/**
 * Info of organization on IMDb.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("Organization")
public class ImdbOrganization extends BaseImdbObject {

    private String url;
}
