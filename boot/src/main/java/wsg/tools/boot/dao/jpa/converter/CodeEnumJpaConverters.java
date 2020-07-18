package wsg.tools.boot.dao.jpa.converter;

import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.pojo.enums.TypeEnum;
import wsg.tools.common.jackson.intf.CodeSerializable;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.video.enums.MarkEnum;

import javax.persistence.Converter;

/**
 * Converters for {@link Enum}.
 * <p>
 * All enums implement {@link CodeSerializable<Integer>}
 * and stored in form of code obtained by {@link CodeSerializable#getCode()}.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class CodeEnumJpaConverters {

    @Converter(autoApply = true)
    public static class StatusEnumConverter extends CodeEnumConverter<MarkEnum> {
        public StatusEnumConverter() {
            super(MarkEnum.class);
        }
    }

    @Converter(autoApply = true)
    public static class TypeEnumConverter extends CodeEnumConverter<TypeEnum> {
        public TypeEnumConverter() {
            super(TypeEnum.class);
        }
    }

    @Converter(autoApply = true)
    public static class ArchivedEnumConverter extends CodeEnumConverter<ArchivedEnum> {
        public ArchivedEnumConverter() {
            super(ArchivedEnum.class);
        }
    }

    static class CodeEnumConverter<E extends Enum<E> & CodeSerializable<Integer>> extends BaseNonNullConverter<E, Integer> {

        protected Class<E> eClass;

        CodeEnumConverter(Class<E> eClass) {
            this.eClass = eClass;
        }

        @Override
        protected E deserialize(Integer dbData) {
            return EnumUtilExt.deserializeCode(dbData, eClass);
        }

        @Override
        protected Integer serialize(E attribute) {
            return attribute.getCode();
        }
    }
}
