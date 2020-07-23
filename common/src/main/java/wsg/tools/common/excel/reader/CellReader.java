package wsg.tools.common.excel.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * Interface to read a cell.
 *
 * @param <JavaType> corresponding java type of the cell
 * @author Kingen
 * @since 2020/7/22
 */
public class CellReader<JavaType> {

    public static final CellReader<String> STRING_CELL_READER = new CellReader<>(String.class);
    private final Class<JavaType> targetClass;
    private final TypeReference<JavaType> typeReference;

    protected CellReader(Class<JavaType> targetClass, TypeReference<JavaType> typeReference) {
        this.targetClass = targetClass;
        this.typeReference = typeReference;
    }

    public CellReader(Class<JavaType> targetClass) {
        this(targetClass, null);
    }

    public CellReader(TypeReference<JavaType> typeReference) {
        this(null, typeReference);
    }

    /**
     * Read value of the given cell
     *
     * @param cell   given cell
     * @param mapper current {@link ObjectMapper}
     * @return value
     */
    public JavaType readValue(Cell cell, ObjectMapper mapper) {
        Object value = getCellValue(cell);
        if (value == null) {
            return null;
        }

        if (ClassUtils.isAssignable(value.getClass(), targetClass)) {
            return targetClass.cast(value);
        }

        Class<?> middleType = getMiddleType();
        Object middleValue;
        if (middleType != null) {
            if (ClassUtils.isAssignable(value.getClass(), middleType)) {
                middleValue = middleType.cast(value);
            } else {
                middleValue = mapper.convertValue(value, middleType);
            }
        } else {
            middleValue = value;
        }

        if (typeReference != null) {
            return mapper.convertValue(middleValue, typeReference);
        }

        if (ClassUtils.isAssignable(middleValue.getClass(), targetClass)) {
            return targetClass.cast(middleValue);
        }
        return mapper.convertValue(middleValue, targetClass);
    }

    /**
     * Returns middle type between the java type and the cell type.
     *
     * @return middle type
     */
    protected Class<?> getMiddleType() {
        return null;
    }

    /**
     * Returns value of the given cell.
     *
     * @param cell given cell
     * @return value
     */
    private Object getCellValue(Cell cell) {
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
