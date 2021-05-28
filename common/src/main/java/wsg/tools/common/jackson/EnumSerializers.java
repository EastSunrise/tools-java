package wsg.tools.common.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * Provides common serializers to serialize enums.
 *
 * @author Kingen
 * @since 2021/5/26
 */
public final class EnumSerializers {

    private EnumSerializers() {
    }

    public static <E extends Enum<E> & CodeSupplier> JsonSerializer<E> ofCode(Class<E> enumClass) {
        return new StdSerializer<>(enumClass) {
            @Override
            public void serialize(E value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
                gen.writeString(value.getCode());
            }
        };
    }

    public static <E extends Enum<E> & IntCodeSupplier>
    JsonSerializer<E> ofIntCode(Class<E> enumClass) {
        return new StdSerializer<>(enumClass) {
            @Override
            public void serialize(E value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
                gen.writeNumber(value.getCode());
            }
        };
    }
}
