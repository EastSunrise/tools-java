package wsg.tools.internet.info.adult.midnight;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.info.adult.view.ActressSupplier;

/**
 * An item with a formal adult entry.
 *
 * @author Kingen
 * @see MidnightSite#findFormalEntry(int)
 * @since 2021/3/10
 */
public class MidnightFormalEntry extends MidnightEntry implements ActressSupplier {

    private List<String> actresses;

    MidnightFormalEntry(int id, String title, LocalDateTime addTime, List<URL> images) {
        super(id, title, addTime, images);
    }

    @Nonnull
    @Override
    public List<String> getActresses() {
        return actresses;
    }

    void setActresses(List<String> actresses) {
        this.actresses = actresses;
    }
}
