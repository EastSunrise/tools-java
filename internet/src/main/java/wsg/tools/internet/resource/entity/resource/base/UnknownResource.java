package wsg.tools.internet.resource.entity.resource.base;

import lombok.Getter;

/**
 * Unknown resource.
 *
 * @author Kingen
 * @since 2020/11/1
 */
@Getter
public class UnknownResource implements Resource {

    private final String title;

    public UnknownResource(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "UnknownResource{" +
                "title='" + title + '\'' +
                '}';
    }
}
