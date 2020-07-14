package wsg.tools.common.jackson.serializer;

import wsg.tools.common.jackson.intf.TitleSerializable;

/**
 * Serialize a object implementing {@link TitleSerializable} to a title.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class TitleSerializer<JavaType extends TitleSerializable> extends AbstractNonNullSerializer<JavaType, String> {

    protected TitleSerializer(Class<JavaType> javaType) {
        super(javaType, String.class);
    }

    public static <T extends TitleSerializable> TitleSerializer<T> getInstance(Class<T> type) {
        return new TitleSerializer<>(type);
    }

    @Override
    public String apply(JavaType javaType) {
        return javaType.getTitle();
    }
}
