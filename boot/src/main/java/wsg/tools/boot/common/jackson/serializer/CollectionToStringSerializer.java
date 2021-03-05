package wsg.tools.boot.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import wsg.tools.common.jackson.serializer.AbstractNonNullSerializer;

/**
 * Serialize the Java container type, {@link Collection}, to a String.
 *
 * @author Kingen
 * @since 2021/3/5
 */
public class CollectionToStringSerializer extends AbstractNonNullSerializer<Collection<?>> {

    private final String delimiter;

    public CollectionToStringSerializer(String delimiter) {
        super(Collection.class, false);
        this.delimiter = delimiter;
    }

    @Override
    protected void serializeNonNull(Collection<?> values, JsonGenerator gen,
        SerializerProvider serializers) throws IOException {
        ObjectMapper mapper = (ObjectMapper) gen.getCodec();
        Stream<String> stream = values.stream()
            .map(value -> mapper.convertValue(value, String.class));
        gen.writeString(stream.collect(Collectors.joining(delimiter)));
    }
}
