package wsg.tools.internet.resource.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Base class of resources.
 *
 * @author Kingen
 * @since 2020/9/9
 */
@Getter
@Setter
public abstract class AbstractResource {

    private String title;

    /**
     * Obtains the url of this resource.
     *
     * @return url
     */
    public abstract String getUrl();

    @Override
    public String toString() {
        return "AbstractResource{" +
                "title='" + title + '\'' +
                "url='" + getUrl() + '\'' +
                '}';
    }
}
