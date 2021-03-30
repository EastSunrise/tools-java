package wsg.tools.internet.info.adult.entry;

import java.util.Collections;
import java.util.List;

/**
 * An adult entry performed by formal adult actresses.
 *
 * @author Kingen
 * @since 2021/3/3
 */
public class FormalAdultEntry extends AbstractAdultEntry {

    private final List<String> actresses;

    FormalAdultEntry(String code, List<String> actresses) {
        super(code);
        this.actresses = actresses == null ? null : Collections.unmodifiableList(actresses);
    }

    public List<String> getActresses() {
        return actresses;
    }
}
