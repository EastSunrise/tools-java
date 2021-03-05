package wsg.tools.common.io.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.Beta;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.io.excel.reader.CellReader;
import wsg.tools.common.io.excel.reader.CellToSetter;
import wsg.tools.common.io.excel.writer.CellFromGetter;
import wsg.tools.common.util.function.CreatorSupplier;

/**
 * Factory to convert objects when reading or writing excel-like data.
 * <p>
 * todo improve efficiency
 *
 * @author Kingen
 * @since 2020/7/21
 */
@Beta
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
     * Read data from excel input stream. todo create with a required-args constructor
     *
     * @param readers header-reader map, exact headers must be provided.
     * @param creator supply an instance of target object+
     * @param <T>     target type
     * @return list of objects of target type
     */
    public <T> List<T> readXlsx(InputStream inputStream, Map<String, CellToSetter<T, ?>> readers,
        CreatorSupplier<T> creator) throws IOException {
        return readXlsx(inputStream, readers).stream().map(creator::create)
            .collect(Collectors.toList());
    }

    /**
     * Read data from excel input stream.
     *
     * @param readers header-reader map, exact headers must be provided.
     * @return list of header-value map
     */
    public <V> List<Map<String, V>> readXlsx(InputStream inputStream,
        Map<String, ? extends CellReader<? extends V>> readers) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Iterator<Row> iterator = workbook.getSheetAt(0).iterator();
        Map<String, Integer> headers = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        int column = 0;
        for (Cell cell : iterator.next()) {
            headers.put(cell.getStringCellValue(), column);
            column++;
        }
        List<Map<String, V>> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            Map<String, V> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
            for (Map.Entry<String, ? extends CellReader<? extends V>> entry : readers.entrySet()) {
                Integer index = headers.get(entry.getKey());
                if (index == null) {
                    continue;
                }
                Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (cell == null) {
                    continue;
                }
                map.put(entry.getKey(), entry.getValue().readCell(cell, objectMapper));
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

    /**
     * Write data to stream.
     *
     * @param data    data to write
     * @param writers writers to set cells by column
     */
    public <T> void writeXlsx(OutputStream stream, Iterable<T> data,
        LinkedHashMap<String, CellFromGetter<T, ?>> writers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        int i = 0;
        Row row = sheet.createRow(i);
        i++;
        int j = 0;
        for (String header : writers.keySet()) {
            Cell cell = row.createCell(j);
            j++;
            cell.setCellValue(header);
        }
        for (T t : data) {
            row = sheet.createRow(i);
            i++;
            j = 0;
            for (CellFromGetter<T, ?> writer : writers.values()) {
                Cell cell = row.createCell(j);
                j++;
                writer.setCellFromGetter(cell, t, objectMapper);
                writer.setCellStyleFromGetter(cell, t, workbook);
            }
        }
        workbook.write(stream);
        workbook.close();
    }
}
