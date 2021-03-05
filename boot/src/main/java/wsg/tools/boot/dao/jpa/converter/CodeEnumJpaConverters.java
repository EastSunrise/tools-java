package wsg.tools.boot.dao.jpa.converter;

import javax.persistence.Converter;
import wsg.tools.boot.common.enums.ResourceType;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.internet.movie.common.enums.DoubanMark;

/**
 * Converters for {@link Enum}.
 * <p>
 * All enums implement {@link IntCodeSupplier} and stored in form of code obtained by {@link
 * CodeSupplier#getCode()}.
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class CodeEnumJpaConverters {

    @Converter(autoApply = true)
    public static class MarkEnumConverter extends CodeConverter<DoubanMark, MarkEnumAdapter> {

        public MarkEnumConverter() {
            super(MarkEnumAdapter.class);
        }
    }

    @Converter(autoApply = true)
    public static class ResourceTypeConverter extends CodeEnumConverter<ResourceType> {

        public ResourceTypeConverter() {
            super(ResourceType.class);
        }
    }

    /**
     * Converter between a code and an enum implementing {@link IntCodeSupplier}.
     */
    private static class CodeEnumConverter<E extends Enum<E> & IntCodeSupplier>
        extends BaseNonNullConverter<E, Integer> {

        private final Class<E> eClass;

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
