package wsg.tools.internet.info.adult;

import javax.annotation.Nullable;

/**
 * An adult entry performed by an amateur who appears occasionally.
 *
 * @author Kingen
 * @since 2021/3/9
 */
public class AmateurAdultEntry extends AbstractAdultEntry {

    private final String performer;

    AmateurAdultEntry(String code, String performer) {
        super(code);
        this.performer = performer;
    }

    @Nullable
    public String getPerformer() {
        return performer;
    }
}
