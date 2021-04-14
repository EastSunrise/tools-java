package wsg.tools.internet.info.adult.view;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Represents a supplier of actresses.
 *
 * @author Kingen
 * @since 2021/4/7
 */
public interface ActressSupplier {

    /**
     * Returns the actresses of the entry.
     *
     * @return the actresses
     */
    @Nonnull
    List<String> getActresses();
}
