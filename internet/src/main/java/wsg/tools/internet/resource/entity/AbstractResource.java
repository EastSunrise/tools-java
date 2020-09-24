package wsg.tools.internet.resource.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractResource resource = (AbstractResource) o;
        return Objects.equals(getUrl(), resource.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}
