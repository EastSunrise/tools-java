package wsg.tools.internet.info.adult.midnight;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import wsg.tools.internet.info.adult.view.AmateurSupplier;

/**
 * An item with an amateur adult entry.
 *
 * @author Kingen
 * @see MidnightSite#findAmateurEntry(MidnightColumn, int)
 * @since 2021/3/28
 */
public class MidnightAmateurEntry extends MidnightEntry implements AmateurSupplier {

    private String performer;

    MidnightAmateurEntry(int id, String title, LocalDateTime addTime, List<URL> images) {
        super(id, title, addTime, images);
    }

    @Override
    public String getPerformer() {
        return performer;
    }

    void setPerformer(String performer) {
        this.performer = performer;
    }
}
