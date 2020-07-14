package wsg.tools.internet.video.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import wsg.tools.common.jackson.deserializer.AbstractNonNullDeserializer;
import wsg.tools.internet.video.jackson.deserializer.PubDateDeserializer;
import wsg.tools.internet.video.jackson.deserializer.YearExtDeserializer;

/**
 * A simple module registering video-related serializers.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class VideoModule extends SimpleModule {
    public VideoModule() {
        addDeserializer(YearExtDeserializer.INSTANCE);
        addDeserializer(PubDateDeserializer.INSTANCE);
    }

    private <JavaType, JsonType> void addDeserializer(AbstractNonNullDeserializer<JavaType, JsonType> deserializer) {
        addDeserializer(deserializer.getJavaType(), deserializer);
    }
}
