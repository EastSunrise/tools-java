package wsg.tools.internet.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wsg.tools.common.lang.AssertUtils;

/**
 * Common implementations of {@code ContentHandler}.
 *
 * @author Kingen
 * @since 2021/2/11
 */
public final class ContentHandlers {

    /**
     * Parse the content to a {@link Document}.
     */
    public static final ContentHandler<Document> DOCUMENT_CONTENT_HANDLER = new ContentHandler<>() {
        @Override
        public Document handleContent(String content) {
            return Jsoup.parse(content);
        }

        @Override
        public String suffix() {
            return "html";
        }
    };

    /**
     * Parse the json content to a java object.
     */
    public static <T> JsonHandler<T> getJsonHandler(ObjectMapper mapper, Class<T> clazz) {
        return new JsonHandler<>(mapper, clazz);
    }

    /**
     * Parse the json content to a java object.
     */
    public static <T> JsonHandler<T> getJsonHandler(ObjectMapper mapper, TypeReference<T> reference) {
        return new JsonHandler<>(mapper, reference);
    }

    static class JsonHandler<T> implements ContentHandler<T> {

        private final ObjectMapper mapper;
        private final JavaType type;

        JsonHandler(ObjectMapper mapper, Class<T> clazz) {
            this.mapper = mapper;
            this.type = mapper.constructType(clazz);
        }

        JsonHandler(ObjectMapper mapper, TypeReference<T> reference) {
            this.mapper = mapper;
            this.type = mapper.getTypeFactory().constructType(reference);
        }

        @Override
        public T handleContent(String content) {
            try {
                return mapper.readValue(content, type);
            } catch (JsonProcessingException e) {
                throw AssertUtils.runtimeException(e);
            }
        }

        @Override
        public String suffix() {
            return "json";
        }
    }

}
