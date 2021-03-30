package wsg.tools.common.io.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.csv.CSVPrinter;
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

    /**
     * Print value got from implemented getter.
     */
    public void printFromGetter(CSVPrinter printer, T t, ObjectMapper mapper) throws IOException {
        print(printer, getter.getValue(t), mapper);
    }
}
