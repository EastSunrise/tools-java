package wsg.tools.common.excel.writer;

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
public abstract class BaseCellWriter<V> {

    /**
     * Set value of the given cell.
     *
     * @param cell   given cell
     * @param value  object to write
     * @param mapper mapper to convert values
     */
    public void setCellValue(Cell cell, V value, ObjectMapper mapper) {
        if (setCellValue(cell, value)) {
            return;
        }

        Class<?> middleType = getMiddleType();
        Object middleValue;
        if (middleType != null) {
            middleValue = (ClassUtils.isAssignable(value.getClass(), middleType)) ?
                    middleType.cast(value) :
                    mapper.convertValue(value, middleType);
            if (setCellValue(cell, middleValue)) {
                return;
            }
        } else {
            middleValue = value;
        }

        setCellValue(cell, middleValue.toString());
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
     * Print a value
     */
    public void print(CSVPrinter printer, V value, ObjectMapper mapper) throws IOException {
        printer.print(mapper.convertValue(value, String.class));
    }

    /**
     * Returns middle type between type of the cell and type of java object to write.
     *
     * @return middle type
     */
    protected Class<?> getMiddleType() {
        return null;
    }

    /**
     * Set value of the given cell
     *
     * @param cell  given cell
     * @param value value to set
     * @return whether set successfully
     */
    private boolean setCellValue(Cell cell, Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            cell.setCellValue((String) value);
            return true;
        }
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
            return true;
        }
        if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
            return true;
        }
        if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
            return true;
        }
        if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
            return true;
        }
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
            return true;
        }
        if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
            return true;
        }
        if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
            return true;
        }
        return false;
    }
}

