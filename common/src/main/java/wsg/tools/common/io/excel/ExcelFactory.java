package wsg.tools.common.io.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.function.CreatorSupplier;

/**
 * Factory to convert objects when reading or writing excel-like data.
 * <p>
 * todo improve efficiency
 *
 * @author Kingen
 * @since 2020/7/21
 */
public class ExcelFactory {

    private final ObjectMapper objectMapper;

    private final CSVFormat format;

    public ExcelFactory() {
        this(CSVFormat.DEFAULT);
    }

    public ExcelFactory(CSVFormat format) {
        this(new ObjectMapper(), format);
    }

    public ExcelFactory(ObjectMapper objectMapper) {
        this(objectMapper, CSVFormat.DEFAULT);
    }

    public ExcelFactory(ObjectMapper objectMapper, CSVFormat format) {
        this.objectMapper = objectMapper;
        this.format = format;
    }

    /**
     * Read data from csv input stream.
     *
     * @param readers header-reader map, exact headers must be provided.
     * @param creator supply an instance of target object
     * @param charset given charset
     * @param <T>     target type
     * @return list of objects of target type
     */
    public <T> List<T> readCsv(InputStream inputStream, Map<String, CellToSetter<T, ?>> readers,
        CreatorSupplier<T> creator, Charset charset) throws IOException {
        return readCsv(inputStream, readers, charset).stream().map(creator::create)
            .collect(Collectors.toList());
    }

    /**
     * Read data from csv input stream. Convert each cell from string to target type with Jackson.
     *
     * @param readers header-reader map, exact headers must be provided.
     * @param charset given charset
     * @return list of header-value map
     */
    public List<Map<String, Object>> readCsv(InputStream inputStream,
        Map<String, ? extends CellReader<?>> readers,
        Charset charset) throws IOException {
        CSVFormat csvFormat = format;
        if (MapUtils.isNotEmpty(readers)) {
            csvFormat = csvFormat.withHeader(readers.keySet().toArray(new String[0]))
                .withFirstRecordAsHeader();
        }
        CSVParser parse = CSVParser.parse(inputStream, charset, csvFormat);
        List<Map<String, Object>> list = new ArrayList<>();
        for (CSVRecord record : parse) {
            Map<String, Object> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
            for (Map.Entry<String, ? extends CellReader<?>> entry : readers.entrySet()) {
                CellReader<?> reader = entry.getValue();
                map.put(entry.getKey(),
                    reader.readRecord(record.get(entry.getKey()), objectMapper));
            }
            list.add(map);
        }
        return list;
    }

    /**
     * Write csv data to stream.
     *
     * @param data    data to write to the csv file
     * @param writers writers to handle each column
     */
    public <T> void writeCsv(Appendable out, Iterable<T> data,
        LinkedHashMap<String, CellFromGetter<T, ?>> writers)
        throws IOException {
        CSVPrinter printer = format.print(out);
        printer.printRecord(writers.keySet());
        for (T t : data) {
            for (CellFromGetter<T, ?> writer : writers.values()) {
                writer.printFromGetter(printer, t, objectMapper);
            }
            printer.println();
        }
        printer.close(true);
    }
}
