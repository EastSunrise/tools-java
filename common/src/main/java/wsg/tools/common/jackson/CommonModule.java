package wsg.tools.common.jackson;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import wsg.tools.common.jackson.deserializer.AbstractNonNullDeserializer;
import wsg.tools.common.jackson.deserializer.EnumAkaDeserializers;
import wsg.tools.common.jackson.deserializer.EnumCodeDeserializers;
import wsg.tools.common.jackson.intf.AkaPredicate;
import wsg.tools.common.jackson.intf.CodeSupplier;
import wsg.tools.common.jackson.intf.TextSupplier;
import wsg.tools.common.jackson.intf.TitleSupplier;
import wsg.tools.common.jackson.serializer.CodeSerializer;
import wsg.tools.common.jackson.serializer.TextSerializer;
import wsg.tools.common.jackson.serializer.TitleSerializer;

/**
 * A common module extending common methods to add serializers or deserializers.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class CommonModule extends SimpleModule {

    public CommonModule() {
    }

    @Override
    public CommonModule addSerializer(JsonSerializer<?> ser) {
        super.addSerializer(ser);
        return this;
    }

    public <JavaType, JsonType> CommonModule addDeserializer(AbstractNonNullDeserializer<JavaType, JsonType> deserializer) {
        addDeserializer(deserializer.getJavaType(), deserializer);
        return this;
    }

    public <Code, JavaType extends CodeSupplier<Code>> CommonModule addCodeSerializer(Class<Code> codeClass, Class<JavaType> javaType) {
        return addSerializer(CodeSerializer.getInstance(codeClass, javaType));
    }

    public <Code, E extends Enum<E> & CodeSupplier<Code>> CommonModule addCodeEnumDeserializer(Class<Code> codeClass, Class<E> eClass) {
        return addDeserializer(EnumCodeDeserializers.getDeserializer(codeClass, eClass));
    }

    public <Code, E extends Enum<E> & CodeSupplier<Code>> CommonModule addCodeEnumSerAndDeser(Class<Code> codeClass, Class<E> eClass) {
        return addDeserializer(EnumCodeDeserializers.getDeserializer(codeClass, eClass))
                .addSerializer(CodeSerializer.getInstance(codeClass, eClass));
    }

    public <JavaType extends TitleSupplier> CommonModule addEnumTitleSerializer(Class<JavaType> javaType) {
        return addSerializer(TitleSerializer.getInstance(javaType));
    }

    public <JavaType extends TextSupplier> CommonModule addTextSerializer(Class<JavaType> javaType) {
        return addSerializer(TextSerializer.getInstance(javaType));
    }

    public <Aka, E extends Enum<E> & AkaPredicate<Aka>> CommonModule addAkaEnumDeserializer(Class<Aka> akaClass, Class<E> eClass) {
        return addDeserializer(EnumAkaDeserializers.getDeserializer(akaClass, eClass));
    }

    public <E extends Enum<E> & AkaPredicate<String>> CommonModule addStringAkaEnumDeserializer(Class<E> eClass) {
        return addAkaEnumDeserializer(String.class, eClass);
    }

}
