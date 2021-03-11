package wsg.tools.internet.info.adult.common;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import wsg.tools.common.lang.AssertUtils;

/**
 * An adult entry performed by formal adult actresses.
 *
 * @author Kingen
 * @since 2021/3/3
 */
@Getter
public class FormalAdultEntry extends AdultEntry {

    private final List<String> actresses;

    FormalAdultEntry(String code, List<String> actresses) {
        super(code);
        AssertUtils.require(actresses, CollectionUtils::isNotEmpty, "Empty Actresses");
        this.actresses = Collections.unmodifiableList(actresses);
    }
}
