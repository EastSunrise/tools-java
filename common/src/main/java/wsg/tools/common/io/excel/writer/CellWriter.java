package wsg.tools.common.io.excel.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ClassUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Interface to write a cell.
 *
 * @param <V> corresponding java type of the cell
 * @author Kingen
 * @since 2020/7/21
 */
public class CellWriter<V> {

    /**
     * Must be one of types mentioned in {@link #setCellValue(Cell, Object, Class)}.
     */
    private final Class<?> targetType;

    public CellWriter() {
        this.targetType = null;
    }

    public CellWriter(Class<?> targetType) {
        this.targetType = targetType;
    }

    /**
     * Set value of the given cell.
     * <p>
     * If type of the value is one of those mentioned in {@link #setCellValue(Cell, Object, Class)}, it'll set directly.
     * If not, the value will be convert to the target type, and then try to set the cell again.
     * If still not, it will throw an exception.
     *
     * @param cell   given cell
     * @param value  object to write
     * @param mapper mapper to convert values
     */
    public void setCellValue(Cell cell, V value, ObjectMapper mapper) {
        if (value == null) {
            return;
        }
        if (setCellValue(cell, value, value.getClass())) {
            return;
        }

        if (targetType != null) {
            Object targetValue = (ClassUtils.isAssignable(value.getClass(), targetType)) ? targetType.cast(value) : mapper.convertValue(value, targetType);
            if (setCellValue(cell, targetValue, targetType)) {
                return;
            }
            throw new IllegalArgumentException("Can't write type of " + targetType + " to a cell.");
        }
        throw new IllegalArgumentException("Can't write type of " + value.getClass() + " to a cell.");
    }

    /**
     * Set style of the given cell.
     *
     * @param cell     given cell
     * @param v        value of the cell
     * @param workbook target workbook
     */
    public void setCellStyle(Cell cell, V v, Workbook workbook) {
    }

    /**
     * Print a value to the csv
     */
    public void print(CSVPrinter printer, V value, ObjectMapper mapper) throws IOException {
        printer.print(mapper.convertValue(value, String.class));
    }

    /**
     * Set value of the given cell.
     *
     * @return true if the value is null or the type of the value is one of String, Boolean, LocalDate, LocalDateTime,
     * Double, Date, Calendar, or RichTextString.
     */
    private boolean setCellValue(Cell cell, Object value, Class<?> clazz) {
        if (value == null) {
            return true;
        }
        if (String.class.equals(clazz)) {
            cell.setCellValue((String) value);
            return true;
        }
        if (Boolean.class.equals(clazz)) {
            cell.setCellValue((Boolean) value);
            return true;
        }
        if (LocalDate.class.equals(clazz)) {
            cell.setCellValue((LocalDate) value);
            return true;
        }
        if (LocalDateTime.class.equals(clazz)) {
            cell.setCellValue((LocalDateTime) value);
            return true;
        }
        if (Double.class.equals(clazz)) {
            cell.setCellValue((Double) value);
        }
        if (Date.class.equals(clazz)) {
            cell.setCellValue((Date) value);
        }
        if (Calendar.class.equals(clazz)) {
            cell.setCellValue((Calendar) value);
        }
        if (RichTextString.class.equals(clazz)) {
            cell.setCellValue((RichTextString) value);
        }
        return false;
    }
}

