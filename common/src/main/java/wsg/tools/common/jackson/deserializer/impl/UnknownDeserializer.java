package wsg.tools.common.jackson.deserializer.impl;

import lombok.extern.slf4j.Slf4j;
import wsg.tools.common.jackson.deserializer.base.AbstractStringDeserializer;

/**
 * Deserialize unknown properties to string.
 *
 * @author Kingen
 * @since 2020/6/28
 */
@Slf4j
public class UnknownDeserializer extends AbstractStringDeserializer<String> {
    @Override
    public String toNonNullT(String s) {
        log.warn("Unknown: {}", s);
        return s;
    }
}
