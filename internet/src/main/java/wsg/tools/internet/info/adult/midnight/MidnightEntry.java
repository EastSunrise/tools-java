package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import wsg.tools.internet.info.adult.AdultEntry;

/**
 * An entry with an adult entry.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public class MidnightEntry extends BaseMidnightItem {

    private AdultEntry entry;
    private List<String> images;

    MidnightEntry(int id, String title, LocalDateTime release) {
        super(id, title, release);
    }

    public AdultEntry getEntry() {
        return entry;
    }

    void setEntry(AdultEntry entry) {
        this.entry = entry;
    }

    public List<String> getImages() {
        return Collections.unmodifiableList(images);
    }

    void setImages(List<String> images) {
        this.images = Collections.unmodifiableList(images);
    }
}
