package wsg.tools.common.jackson;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Type reference for parameterized type.
 *
 * @author Kingen
 * @since 2020/12/26
 */
public class ParameterizedTypeReference extends TypeReference<Object> {

    private final Type type;

    public ParameterizedTypeReference(ParameterizedType parameterizedType) {
        this.type = parameterizedType.getActualTypeArguments()[0];
    }

    @Override
    public Type getType() {
        return type;
    }
}
