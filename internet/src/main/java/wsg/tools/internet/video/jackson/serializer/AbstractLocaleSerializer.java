package wsg.tools.internet.video.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import wsg.tools.internet.video.enums.AbstractLocale;
import wsg.tools.internet.video.enums.Country;
import wsg.tools.internet.video.enums.Language;

import java.io.IOException;

/**
 * Serialize {@link AbstractLocale<T>} to a code.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class AbstractLocaleSerializer<T extends AbstractLocale<T>> extends JsonSerializer<T> {

    public static final AbstractLocaleSerializer<Language> LANGUAGE = new AbstractLocaleSerializer<>();
    public static final AbstractLocaleSerializer<Country> COUNTRY = new AbstractLocaleSerializer<>();

    private AbstractLocaleSerializer() {
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getCode());
    }
}
