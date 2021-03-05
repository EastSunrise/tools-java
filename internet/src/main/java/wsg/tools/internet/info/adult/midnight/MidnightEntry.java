package wsg.tools.internet.info.adult.midnight;

import java.util.List;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.info.adult.AdultEntry;

/**
 * An entry with an adult entry.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public class MidnightEntry implements TitleSupplier {

    private final String title;
    private AdultEntry entry;
    private List<String> images;

    MidnightEntry(String title) {
        this.title = AssertUtils.requireNotBlank(title);
    }

    @Override
    public String getTitle() {
        return title;
    }

    public AdultEntry getEntry() {
        return entry;
    }

    void setEntry(AdultEntry entry) {
        this.entry = entry;
    }

    public List<String> getImages() {
        return images;
    }

    void setImages(List<String> images) {
        this.images = images;
    }
}
