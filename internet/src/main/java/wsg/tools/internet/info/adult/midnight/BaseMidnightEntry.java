package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;

/**
 * An item with images or an adult entry .
 *
 * @author Kingen
 * @see MidnightAlbum
 * @see MidnightAdultEntry
 * @since 2021/3/2
 */
public abstract class BaseMidnightEntry extends BaseMidnightItem {

    BaseMidnightEntry(int id, String title, LocalDateTime release) {
        super(id, title, release);
    }
}
