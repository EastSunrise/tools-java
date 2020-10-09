package wsg.tools.internet.resource.entity.title;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.resource.AbstractResource;

import java.util.Set;

/**
 * Detail of a title.
 *
 * @author Kingen
 * @since 2020/9/10
 */
@Getter
@Setter
public class BaseDetail {

    private Set<AbstractResource> resources;
}
