package wsg.tools.internet.resource.movie;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.base.AbstractLink;

/**
 * A basic item with a title and contained links.
 *
 * @author Kingen
 * @since 2020/9/10
 */
@Getter
public class BasicItem {

    private String title;
    private List<AbstractLink> links;
    private List<InvalidResourceException> exceptions;

    void setTitle(String title) {
        this.title = title;
    }

    void setLinks(List<AbstractLink> links) {
        this.links = Collections.unmodifiableList(links);
    }

    void setExceptions(List<InvalidResourceException> exceptions) {
        this.exceptions = Collections.unmodifiableList(exceptions);
    }
}
