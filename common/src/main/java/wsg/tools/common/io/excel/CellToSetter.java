package wsg.tools.common.io.excel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import wsg.tools.common.util.function.SetterBiConsumer;

/**
 * Read a cell as a property value of target object {@link T}.
 *
 * @param <T> target type to get, corresponding to the whole row
 * @param <V> corresponding java type of the cell
 * @author Kingen
 * @since 2020/7/24
 */
public class CellToSetter<T, V> extends CellReader<V> {

    private final SetterBiConsumer<? super T, ? super V> setter;

    public CellToSetter(Class<V> clazz, SetterBiConsumer<? super T, ? super V> setter) {
        super(clazz);
        this.setter = setter;
    }

    public CellToSetter(TypeReference<V> typeReference,
        SetterBiConsumer<? super T, ? super V> setter) {
        super(typeReference);
        this.setter = setter;
    }

    /**
     * Read the given value and set the corresponding property of the target object.
     */
    public void readRecordToSetter(String value, ObjectMapper mapper, T t) {
        setter.setValue(t, readRecord(value, mapper));
    }
}
