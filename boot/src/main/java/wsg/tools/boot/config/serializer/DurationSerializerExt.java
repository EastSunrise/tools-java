package wsg.tools.boot.config.serializer;

import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import wsg.tools.common.constant.Constants;

import java.time.Duration;

/**
 * Extended serializer for Java 8 temporal {@link Duration}s.
 *
 * @author Kingen
 * @since 2020/7/14
 */
public class DurationSerializerExt extends DurationSerializer {

    public static final DurationSerializerExt INSTANCE = new DurationSerializerExt();

    protected DurationSerializerExt() {
        super(DurationSerializer.INSTANCE, null, Constants.NULL_FORMATTER);
    }
}
