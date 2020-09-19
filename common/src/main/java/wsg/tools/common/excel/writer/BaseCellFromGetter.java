package wsg.tools.common.excel.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import wsg.tools.common.function.GetterFunction;

import java.io.IOException;

/**
 * Get a property value of target object {@link T} and write to the cell.
 *
 * @param <T> source object, corresponding to the whole row
 * @param <V> corresponding java type of the cell
 * @author Kingen
 * @since 2020/7/24
 */
public abstract class BaseCellFromGetter<T, V> extends BaseCellWriter<V> implements GetterFunction<T, V> {

    /**
     * Set the given cell with value got from implemented getter.
     */
    public void setCellFromGetter(Cell cell, T t, ObjectMapper mapper) {
        super.setCellValue(cell, getValue(t), mapper);
    }

    /**
     * Set style of the given cell.
     */
    public void setCellStyleFromGetter(Cell cell, T t, Workbook workbook) {
        super.setCellStyle(cell, getValue(t), workbook);
    }

    /**
     * Print value got from implemented getter.
     */
    public void printFromGetter(CSVPrinter printer, T t, ObjectMapper mapper) throws IOException {
        super.print(printer, getValue(t), mapper);
    }
}
