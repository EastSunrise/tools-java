package wsg.tools.common.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import wsg.tools.common.jackson.deserializer.EnumAkaDeserializer;
import wsg.tools.common.jackson.deserializer.EnumCodeDeserializer;
import wsg.tools.common.jackson.intf.AkaSerializable;
import wsg.tools.common.jackson.intf.CodeSerializable;
import wsg.tools.common.jackson.intf.TextSerializable;
import wsg.tools.common.jackson.intf.TitleSerializable;
import wsg.tools.common.jackson.serializer.CodeSerializer;
import wsg.tools.common.jackson.serializer.TextSerializer;
import wsg.tools.common.jackson.serializer.TitleSerializer;

/**
 * A simple module registering code-related serializers.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class CodeModule extends SimpleModule {

    public CodeModule() {
    }

    public <Code, JavaType extends CodeSerializable<Code>> CodeModule addCodeSerializer(Class<Code> codeClass, Class<JavaType> javaType) {
        addSerializer(CodeSerializer.getInstance(codeClass, javaType));
        return this;
    }

    public <Code, E extends Enum<E> & CodeSerializable<Code>> CodeModule addCodeEnumDeserializer(Class<Code> codeClass, Class<E> eClass) {
        addDeserializer(eClass, EnumCodeDeserializer.getInstance(codeClass, eClass));
        return this;
    }

    public <JavaType extends TitleSerializable> CodeModule addTitleSerializer(Class<JavaType> javaType) {
        addSerializer(TitleSerializer.getInstance(javaType));
        return this;
    }

    public <JavaType extends TextSerializable> CodeModule addTextSerializer(Class<JavaType> javaType) {
        addSerializer(TextSerializer.getInstance(javaType));
        return this;
    }

    public <Aka, E extends Enum<E> & AkaSerializable<Aka>> CodeModule addAkaEnumDeserializer(Class<Aka> akaClass, Class<E> eClass) {
        addDeserializer(eClass, EnumAkaDeserializer.getInstance(akaClass, eClass));
        return this;
    }

}
