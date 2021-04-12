package wsg.tools.internet.info.adult.west;

import wsg.tools.internet.info.adult.view.Describable;
import wsg.tools.internet.info.adult.view.Tagged;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;
import wsg.tools.internet.info.adult.view.VideoSupplier;

/**
 * A western adult entry, including an integer identifier, a title, a video source, tags, and also
 * being describable.
 *
 * @author Kingen
 * @since 2021/4/10
 */
public interface WesternAdultEntry extends TitledAdultEntry, Describable,
    VideoSupplier, Tagged {

}
