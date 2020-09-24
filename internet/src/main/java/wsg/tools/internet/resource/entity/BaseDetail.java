package wsg.tools.internet.resource.entity;

import lombok.Getter;
import lombok.Setter;

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
