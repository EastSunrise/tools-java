package wsg.tools.boot.dao.jpa.converter;

import wsg.tools.common.function.CodeSupplier;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.video.enums.MarkEnum;

import javax.persistence.Converter;

/**
 * Converters for {@link Enum}.
 * <p>
 * All enums implement {@link CodeSupplier <Integer>}
 * and stored in form of code obtained by {@link CodeSupplier#getCode()}.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class CodeEnumJpaConverters {

    @Converter(autoApply = true)
    public static class MarkEnumConverter extends CodeEnumConverter<MarkEnum> {
        public MarkEnumConverter() {
            super(MarkEnum.class);
        }
    }

    static class CodeEnumConverter<E extends Enum<E> & CodeSupplier<Integer>> extends BaseNonNullConverter<E, Integer> {

        protected final Class<E> eClass;

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
