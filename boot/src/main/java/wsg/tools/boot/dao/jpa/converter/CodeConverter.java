package wsg.tools.boot.dao.jpa.converter;

import java.util.function.Supplier;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * Convert an object of type {@code T} to a code.
 *
 * @param <E> type of the adapter to convert value between {@code T} and a code
 */
class CodeConverter<T, E extends Enum<E> & IntCodeSupplier & Supplier<T> & AkaPredicate<T>>
    extends BaseNonNullConverter<T, Integer> {

    private final Class<E> eClass;

    CodeConverter(Class<E> eClass) {
        this.eClass = eClass;
    }

    @Override
    protected T deserialize(Integer dbData) {
        return EnumUtilExt.valueOfCode(eClass, dbData).get();
    }

    @Override
    protected Integer serialize(T attribute) {
        return EnumUtilExt.valueOfAka(eClass, attribute).getCode();
    }
}
