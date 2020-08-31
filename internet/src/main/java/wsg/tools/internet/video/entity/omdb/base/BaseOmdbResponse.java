package wsg.tools.internet.video.entity.omdb.base;

import lombok.Getter;
import lombok.Setter;

/**
 * Base response of OMDb.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public abstract class BaseOmdbResponse {

    private Boolean response;
    private String error;
}
