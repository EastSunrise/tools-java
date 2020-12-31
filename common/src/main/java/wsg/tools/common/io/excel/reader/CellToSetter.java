package wsg.tools.common.io.excel.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
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

    private final SetterBiConsumer<T, V> setter;

    public CellToSetter(Class<V> clazz, SetterBiConsumer<T, V> setter) {
        super(clazz);
        this.setter = setter;
    }

    public CellToSetter(TypeReference<V> typeReference, SetterBiConsumer<T, V> setter) {
        super(typeReference);
        this.setter = setter;
    }

    /**
     * Read the given cell and set the corresponding property of the target object.
     */
    public void readCellToSetter(Cell cell, ObjectMapper mapper, T t) {
        setter.setValue(t, readCell(cell, mapper));
    }

    /**
     * Read the given value and set the corresponding property of the target object.
     */
    public void readRecordToSetter(String value, ObjectMapper mapper, T t) {
        setter.setValue(t, readRecord(value, mapper));
    }
}
