package wsg.tools.boot.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import wsg.tools.common.jackson.serializer.AbstractNonNullSerializer;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Serialize Java container types, such as {@link Collection}, {@link Map}.
 *
 * @author Kingen
 * @since 2020/7/22
 */
public abstract class ContainerSerializers {

    public static class CollectionToStringSerializer extends AbstractNonNullSerializer<Collection<?>, String> {

        private final String delimiter;

        protected CollectionToStringSerializer(String delimiter) {
            super(Collection.class, String.class, false);
            this.delimiter = delimiter;
        }

        public static CollectionToStringSerializer getInstance(String delimiter) {
            return new CollectionToStringSerializer(delimiter);
        }

        @Override
        protected void serializeNonNull(Collection<?> values, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            ObjectMapper mapper = (ObjectMapper) gen.getCodec();
            Stream<String> stream = values.stream().map(value -> mapper.convertValue(value, String.class));
            gen.writeString(stream.collect(Collectors.joining(delimiter)));
        }
    }
}
