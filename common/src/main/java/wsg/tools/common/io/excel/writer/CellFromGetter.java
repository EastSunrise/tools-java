package wsg.tools.common.io.excel.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import wsg.tools.common.util.function.GetterFunction;

/**
 * Get a property value of target object {@link T} and write to the cell.
 *
 * @param <T> source object, corresponding to the whole row
 * @param <V> corresponding java type of the cell
 * @author Kingen
 * @since 2020/7/24
 */
public class CellFromGetter<T, V> extends CellWriter<V> {

    private final GetterFunction<T, V> getter;

    public CellFromGetter(GetterFunction<T, V> getter) {
        this.getter = Objects.requireNonNull(getter);
    }

    public CellFromGetter(Class<?> targetType, GetterFunction<T, V> getter) {
        super(targetType);
        this.getter = getter;
    }

    /**
     * Set the given cell with value got from implemented getter.
     */
    public void setCellFromGetter(Cell cell, T t, ObjectMapper mapper) {
        setCellValue(cell, getter.getValue(t), mapper);
    }

    /**
     * Set style of the given cell.
     */
    public void setCellStyleFromGetter(Cell cell, T t, Workbook workbook) {
        setCellStyle(cell, getter.getValue(t), workbook);
    }

    /**
     * Print value got from implemented getter.
     */
    public void printFromGetter(CSVPrinter printer, T t, ObjectMapper mapper) throws IOException {
        print(printer, getter.getValue(t), mapper);
    }
}
