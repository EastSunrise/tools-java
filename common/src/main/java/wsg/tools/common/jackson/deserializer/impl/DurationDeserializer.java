package wsg.tools.common.jackson.deserializer.impl;

import wsg.tools.common.jackson.deserializer.base.AbstractStringDeserializer;

import java.time.Duration;

/**
 * Deserialize a text like 'PT3M' to an instance of {@link Duration}.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class DurationDeserializer extends AbstractStringDeserializer<Duration> {
    @Override
    public Duration toNonNullT(String s) {
        return Duration.parse(s);
    }
}
