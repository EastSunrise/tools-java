package wsg.tools.common.io.excel.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Interface to read a cell.
 *
 * @param <V> corresponding java type of the cell
 * @author Kingen
 * @since 2020/7/22
 */
public class CellReader<V> {

    private final Type type;

    public CellReader(Class<V> clazz) {
        this.type = clazz;
    }

    public CellReader(TypeReference<V> typeReference) {
        this.type = typeReference.getType();
    }

    /**
     * Read value of a cell of Excel.
     */
    public V readCell(Cell cell, ObjectMapper mapper) {
        Object value = readValue(cell);
        if (value == null) {
            return null;
        }

        JavaType javaType = mapper.getTypeFactory().constructType(getType());
        return mapper.convertValue(value, javaType);
    }

    /**
     * Read value of a record of csv
     */
    public V readRecord(String value, ObjectMapper mapper) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        JavaType javaType = mapper.getTypeFactory().constructType(getType());
        return mapper.convertValue(value, javaType);
    }

    protected Type getType() {
        return type;
    }

    /**
     * todo Read value of the given cell
     *
     * @param cell given cell, not null
     * @return value
     */
    private Object readValue(Cell cell) {
        if (type instanceof Class) {
            if (String.class.equals(type)) {
                return cell.getStringCellValue();
            }
            if (Boolean.class.equals(type)) {
                return cell.getBooleanCellValue();
            }
            if (LocalDate.class.equals(type)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }
            if (LocalDateTime.class.equals(type)) {
                return cell.getLocalDateTimeCellValue();
            }
            if (Double.class.equals(type)) {
                return cell.getNumericCellValue();
            }
            if (Date.class.equals(type)) {
                return cell.getDateCellValue();
            }
        }
        return null;
    }
}
