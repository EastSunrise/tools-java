package wsg.tools.internet.base.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import wsg.tools.internet.base.ContentHandler;
import wsg.tools.internet.common.UnexpectedException;

/**
 * An implementation of {@link ContentHandler} to handle the json response and return as a Java
 * object.
 *
 * @author Kingen
 * @since 2021/3/1
 */
public class JsonHandler<T> implements ContentHandler<T> {

    private final ObjectMapper mapper;
    private final JavaType type;

    public JsonHandler(ObjectMapper mapper, Class<T> clazz) {
        this.mapper = mapper;
        this.type = mapper.constructType(clazz);
    }

    public JsonHandler(ObjectMapper mapper, TypeReference<T> reference) {
        this.mapper = mapper;
        this.type = mapper.getTypeFactory().constructType(reference);
    }

    @Override
    public T handleContent(String content) {
        try {
            return mapper.readValue(content, type);
        } catch (JsonProcessingException e) {
            throw new UnexpectedException(e);
        }
    }

    @Override
    public String suffix() {
        return "json";
    }
}
