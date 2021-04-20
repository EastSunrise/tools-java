package wsg.tools.boot.dao.api.adapter;

import javax.annotation.Nonnull;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.info.adult.view.Describable;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.PreviewSupplier;
import wsg.tools.internet.info.adult.view.Tagged;
import wsg.tools.internet.info.adult.view.VideoSupplier;

/**
 * A western adult entry, including an integer identifier, a title, a video source, tags, and also
 * being describable.
 *
 * @author Kingen
 * @see wsg.tools.boot.pojo.entity.adult.WesternAdultVideoEntity
 * @since 2021/4/10
 */
public interface WesternAdultEntry extends TitleSupplier, CoverSupplier, PreviewSupplier,
    DurationSupplier, Describable, VideoSupplier, Tagged {

    /**
     * Returns the categories to which the entry may belong.
     *
     * @return the categories of the entry
     */
    @Nonnull
    String[] getCategories();
}
