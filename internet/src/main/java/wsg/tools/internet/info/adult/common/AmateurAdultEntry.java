package wsg.tools.internet.info.adult.common;

import lombok.Getter;
import wsg.tools.common.lang.AssertUtils;

/**
 * An adult entry performed by laywomen who appear occasionally.
 *
 * @author Kingen
 * @since 2021/3/9
 */
@Getter
public class AmateurAdultEntry extends AdultEntry {

    private final String performer;

    AmateurAdultEntry(String code, String performer) {
        super(code);
        this.performer = AssertUtils.requireNotBlank(performer, "Performer");
    }
}
