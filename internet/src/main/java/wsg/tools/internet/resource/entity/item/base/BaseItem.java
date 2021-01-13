package wsg.tools.internet.resource.entity.item.base;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;

import java.util.List;

/**
 * Detail of a base item.
 *
 * @author Kingen
 * @since 2020/9/10
 */
@Getter
@Setter
public class BaseItem {

    private String title;
    private List<ValidResource> resources;
    private List<InvalidResourceException> exceptions;
}
