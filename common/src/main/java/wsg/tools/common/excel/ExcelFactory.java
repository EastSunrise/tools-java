package wsg.tools.common.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.excel.reader.CellReader;
import wsg.tools.common.excel.writer.CellWriter;
import wsg.tools.common.excel.writer.ValueSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

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
     * Read a csv input stream with {@link Charset#defaultCharset()} charset.
     */
    public List<Map<String, ?>> readCsv(final InputStream inputStream, Map<String, Class<?>> classes) throws IOException {
        return readCsv(inputStream, classes, Charset.defaultCharset());
    }

    /**
     * Read data from csv input stream. Convert each cell from string to target type with Jackson.
     *
     * @param classes header-class map, exact headers must be provided.
     * @param charset given charset
     * @return list of header-value map
     */
    public List<Map<String, ?>> readCsv(final InputStream inputStream, Map<String, Class<?>> classes, Charset charset) throws IOException {
        CSVFormat format = this.format;
        if (MapUtils.isNotEmpty(classes)) {
            format = format.withHeader(classes.keySet().toArray(new String[0])).withFirstRecordAsHeader();
        }
        CSVParser parse = CSVParser.parse(inputStream, charset, format);
        List<Map<String, ?>> list = new ArrayList<>();
        for (CSVRecord record : parse) {
            Map<String, Object> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
            for (Map.Entry<String, Class<?>> entry : classes.entrySet()) {
                map.put(entry.getKey(), objectMapper.convertValue(record.get(entry.getKey()), entry.getValue()));
            }
            list.add(map);
        }
        return list;
    }

    /**
     * Write csv data to stream.
     *
     * @param data      data to write to the csv file
     * @param suppliers suppliers to handle each column
     */
    public <T> void writeCsv(final Appendable out, List<T> data, LinkedHashMap<String, ValueSupplier<T, ?>> suppliers) throws IOException {
        CSVPrinter printer = format.print(out);
        printer.printRecord(suppliers.keySet());
        for (T t : data) {
            for (ValueSupplier<T, ?> supplier : suppliers.values()) {
                printer.print(objectMapper.convertValue(supplier.getValue(t), String.class));
            }
            printer.println();
        }
        printer.close(true);
    }

    /**
     * Read data from excel input stream.
     *
     * @param readers header-reader map, exact headers must be provided.
     * @return list of header-value map
     */
    public List<Map<String, ?>> readXlsx(final InputStream inputStream, Map<String, CellReader<?>> readers) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Iterator<Row> iterator = workbook.getSheetAt(0).iterator();
        Map<String, Integer> headers = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        int column = 0;
        for (Cell cell : iterator.next()) {
            headers.put(cell.getStringCellValue(), column++);
        }
        List<Map<String, ?>> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            Map<String, Object> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
            for (Map.Entry<String, CellReader<?>> entry : readers.entrySet()) {
                Integer index = headers.get(entry.getKey());
                if (index == null) {
                    continue;
                }
                Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (cell == null) {
                    continue;
                }
                map.put(entry.getKey(), entry.getValue().readValue(cell, objectMapper));
            }
            list.add(map);
        }
        return list;
    }

    /**
     * Write a excel workbook to stream.
     *
     * @param data    data to write
     * @param writers writers to set cells by column
     */
    public <T> void writeXlsx(OutputStream stream, List<T> data, LinkedHashMap<String, CellWriter<T, ?>> writers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        int i = 0;
        Row row = sheet.createRow(i++);
        int j = 0;
        for (String header : writers.keySet()) {
            Cell cell = row.createCell(j++);
            cell.setCellValue(header);
        }
        for (T t : data) {
            row = sheet.createRow(i++);
            j = 0;
            for (CellWriter<T, ?> writer : writers.values()) {
                Cell cell = row.createCell(j++);
                writer.setCellValue(cell, t, objectMapper);
                writer.setCellStyle(cell, t, workbook);
            }
        }
        workbook.write(stream);
        workbook.close();
    }
}
