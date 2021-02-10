package wsg.tools.internet.resource.item;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.InvalidResourceException;

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
    private List<AbstractResource> resources;
    private List<InvalidResourceException> exceptions;
}
