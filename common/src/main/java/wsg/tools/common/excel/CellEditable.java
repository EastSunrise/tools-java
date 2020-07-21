package wsg.tools.common.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;
import wsg.tools.common.converter.ConvertFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Interface to set a cell.
 *
 * @author Kingen
 * @since 2020/7/21
 */

@FunctionalInterface
public interface CellEditable<T, V> extends ValueSupplier<T, V> {
    /**
     * Set value of the given cell.
     *
     * @param cell  cell to set
     * @param value value to write
     */
    static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            cell.setCellValue((String) value);
            return;
        }
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
            return;
        }
        if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
            return;
        }
        if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
            return;
        }
        if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
            return;
        }
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
            return;
        }
        if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
            return;
        }
        if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
            return;
        }
        cell.setCellValue(value.toString());
    }

    /**
     * Set value and style of the given cell
     *
     * @param cell     given cell
     * @param t        object representing current row
     * @param workbook current {@link Workbook}
     * @param factory  factory to convert complex value to a simple object
     */
    default void setCell(Cell cell, T t, Workbook workbook, ConvertFactory<Object> factory) {
        setCellValue(cell, factory.convertValue(getValue(t)));
    }
}

