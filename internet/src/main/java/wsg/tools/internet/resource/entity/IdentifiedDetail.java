package wsg.tools.internet.resource.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Detail of title with ids.
 *
 * @author Kingen
 * @since 2020/9/10
 */
@Getter
@Setter
public class IdentifiedDetail extends BaseDetail {

    private Long dbId;
    private String imdbId;
}
