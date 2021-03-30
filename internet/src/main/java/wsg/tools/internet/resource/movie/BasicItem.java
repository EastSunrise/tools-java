package wsg.tools.internet.resource.movie;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.base.AbstractLink;

/**
 * A basic item with a title and links of resources.
 *
 * @author Kingen
 * @since 2020/9/10
 */
@Getter
class BasicItem {

    private String title;
    private List<AbstractLink> links;
    private List<InvalidResourceException> exceptions;

    void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "the title of an item");
    }

    void setLinks(List<AbstractLink> links) {
        this.links = Collections.unmodifiableList(links);
    }

    void setExceptions(List<InvalidResourceException> exceptions) {
        this.exceptions = Collections.unmodifiableList(exceptions);
    }
}
