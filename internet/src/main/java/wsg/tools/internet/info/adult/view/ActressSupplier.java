package wsg.tools.internet.info.adult.view;

import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.EntityProperty;

/**
 * Represents a supplier of actresses.
 *
 * @author Kingen
 * @since 2021/4/7
 */
@EntityProperty
public interface ActressSupplier {

    /**
     * Returns the actresses of the entry.
     *
     * @return the actresses
     */
    @Nonnull
    List<String> getActresses();
}
