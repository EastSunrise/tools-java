package wsg.tools.common.excel.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import wsg.tools.common.function.SetterBiConsumer;

/**
 * Read a cell as a property value of target object {@link T}.
 *
 * @param <T> target type to get, corresponding to the whole row
 * @param <V> corresponding java type of the cell
 * @author Kingen
 * @since 2020/7/24
 */
public abstract class BaseCellToSetter<T, V> extends CellReader<V> implements SetterBiConsumer<T, V> {

    public BaseCellToSetter(Class<V> clazz) {
        super(clazz);
    }

    public BaseCellToSetter(TypeReference<V> typeReference) {
        super(typeReference);
    }

    /**
     * Read the given cell and set the corresponding property of the target object.
     */
    public void readCellToSet(Cell cell, ObjectMapper mapper, T t) {
        setValue(t, readCell(cell, mapper));
    }

    /**
     * Read the given value and set the corresponding property of the target object.
     */
    public void readRecordToSet(String value, ObjectMapper mapper, T t) {
        setValue(t, readRecord(value, mapper));
    }
}
