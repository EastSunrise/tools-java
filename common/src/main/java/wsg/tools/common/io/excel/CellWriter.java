package wsg.tools.common.io.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.csv.CSVPrinter;

/**
 * Interface to write a cell.
 *
 * @param <V> corresponding java type of the cell
 * @author Kingen
 * @since 2020/7/21
 */
public class CellWriter<V> {

    /**
     * Print a value to the csv
     */
    public void print(CSVPrinter printer, V value, ObjectMapper mapper) throws IOException {
        printer.print(mapper.convertValue(value, String.class));
    }
}
