package wsg.tools.common.io.excel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import org.apache.commons.lang3.StringUtils;

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
     * Read value of a record of csv
     */
    public V readRecord(String value, ObjectMapper mapper) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        JavaType javaType = mapper.getTypeFactory().constructType(type);
        return mapper.convertValue(value, javaType);
    }

    protected Type getType() {
        return type;
    }
}
