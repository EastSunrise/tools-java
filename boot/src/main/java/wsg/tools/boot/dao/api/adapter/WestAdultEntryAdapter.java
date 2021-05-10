package wsg.tools.boot.dao.api.adapter;

import java.util.Set;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.Describable;
import wsg.tools.internet.info.adult.view.VideoSupplier;

/**
 * Adapter that supplies properties to build a {@link wsg.tools.boot.pojo.entity.adult.WesternAdultVideoEntity}.
 *
 * @author Kingen
 * @since 2021/4/29
 */
public interface WestAdultEntryAdapter extends AdultEntryAdapter, VideoSupplier, Describable {

    /**
     * Returns the categories to which the entry may belong.
     *
     * @return the categories of the entry
     */
    @Nonnull
    Set<String> getCategories();
}
