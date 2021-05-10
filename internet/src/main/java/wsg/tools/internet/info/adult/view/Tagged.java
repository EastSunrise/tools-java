package wsg.tools.internet.info.adult.view;

import java.util.Set;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.EntityProperty;

/**
 * Indicates that the entry has tags.
 *
 * @author Kingen
 * @since 2021/4/7
 */
@EntityProperty
public interface Tagged {

    /**
     * Returns the tags of the entry.
     *
     * @return the tags
     */
    @Nonnull
    Set<String> getTags();
}
