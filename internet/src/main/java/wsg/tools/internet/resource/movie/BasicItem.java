package wsg.tools.internet.resource.movie;

import lombok.Getter;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.base.AbstractLink;

import java.util.List;

/**
 * A basic item with a title and contained links.
 *
 * @author Kingen
 * @since 2020/9/10
 */
@Getter
public class BasicItem {

    private String title;
    private List<AbstractLink> resources;
    private List<InvalidResourceException> exceptions;

    void setTitle(String title) {
        this.title = title;
    }

    void setResources(List<AbstractLink> resources) {
        this.resources = resources;
    }

    void setExceptions(List<InvalidResourceException> exceptions) {
        this.exceptions = exceptions;
    }
}
