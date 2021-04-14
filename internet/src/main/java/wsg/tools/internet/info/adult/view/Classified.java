package wsg.tools.internet.info.adult.view;

import javax.annotation.Nonnull;

/**
 * Indicates that the entry can be classified by tags or categories.
 *
 * @author Kingen
 * @since 2021/4/9
 */
public interface Classified extends Tagged {

    /**
     * Returns the categories to which the entry may belong.
     *
     * @return the categories of the entry
     */
    @Nonnull
    String[] getCategories();
}
