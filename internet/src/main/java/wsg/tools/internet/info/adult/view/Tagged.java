package wsg.tools.internet.info.adult.view;

import javax.annotation.Nonnull;

/**
 * Indicates that the entry has tags.
 *
 * @author Kingen
 * @since 2021/4/7
 */
public interface Tagged {

    /**
     * Returns the tags of the entry.
     *
     * @return the tags
     */
    @Nonnull
    String[] getTags();
}
