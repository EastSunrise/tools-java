package wsg.tools.common.io.excel.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.reflect.Type;

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
     * Read a cell of Excel.
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
     * Read a value of csv
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
     * Read value of the given cell
     *
     * @param cell given cell, not null
     * @return value
     */
    private Object readValue(Cell cell) {
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                String value = cell.getStringCellValue();
                return StringUtils.isBlank(value) ? null : value;
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
