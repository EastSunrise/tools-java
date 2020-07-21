package wsg.tools.common.converter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wsg.tools.common.converter.base.BaseConverter;
import wsg.tools.common.converter.base.Converter;
import wsg.tools.common.excel.CellEditable;
import wsg.tools.common.excel.ValueSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Factory to convert objects when reading or writing excel-like data..
 *
 * @author Kingen
 * @since 2020/7/21
 */
public class ExcelFactory extends ConvertFactory<Object> {

    private final CSVFormat format;

    protected ExcelFactory() {
        this(CSVFormat.DEFAULT);
    }

    protected ExcelFactory(CSVFormat format) {
        this.format = format;
    }

    public static ExcelFactory getInstance() {
        return new ExcelFactory();
    }

    @Override
    public ExcelFactory putConverter(BaseConverter<?, ?> converter) {
        super.putConverter(converter);
        return this;
    }

    /**
     * Read a csv input stream with {@link Charset#defaultCharset()} charset.
     *
     * @param function converter from {@link CSVRecord} to target object
     * @param header   headers of table
     * @return list of objects
     */
    public <T> List<T> readCsv(final InputStream inputStream, Converter<CSVRecord, T> function, String... header) throws IOException {
        return readCsv(inputStream, function, Charset.defaultCharset(), header);
    }

    /**
     * Read a csv input stream.
     *
     * @param function converter from {@link CSVRecord} to target object
     * @param charset  given charset
     * @param header   headers of table
     * @return list of objects
     */
    public <T> List<T> readCsv(final InputStream inputStream, Converter<CSVRecord, T> function, Charset charset, String... header) throws IOException {
        CSVFormat format = this.format;
        if (ArrayUtils.isNotEmpty(header)) {
            format = format.withHeader(header).withFirstRecordAsHeader();
        }
        CSVParser parse = CSVParser.parse(inputStream, charset, format);
        List<CSVRecord> records = parse.getRecords();
        return records.stream().map(function::convert).collect(Collectors.toList());
    }

    /**
     * Export a csv file.
     *
     * @param data      data to write to the csv file
     * @param functions functions to handle each column
     */
    public <T> void exportCsv(final Appendable out, List<T> data, Map<String, ValueSupplier<T, ?>> functions) throws IOException {
        CSVPrinter printer = format.print(out);
        printer.printRecord(functions.keySet());
        for (T t : data) {
            for (ValueSupplier<T, ?> function : functions.values()) {
                printer.print(convertValue(function.getValue(t)));
            }
            printer.println();
        }
        printer.close(true);
    }

    /**
     * Export an excel file in form of .xlsx.
     *
     * @param data      data to write
     * @param functions functions to set cells by column
     */
    public <T> void exportXlsx(OutputStream stream, List<T> data, Map<String, CellEditable<T, ?>> functions) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        CreationHelper creationHelper = workbook.getCreationHelper();
        Sheet sheet = workbook.createSheet();

        int i = 0;
        Row row = sheet.createRow(i++);
        int j = 0;
        for (String header : functions.keySet()) {
            Cell cell = row.createCell(j++);
            cell.setCellValue(header);
        }
        for (T t : data) {
            row = sheet.createRow(i++);
            j = 0;
            for (CellEditable<T, ?> function : functions.values()) {
                Cell cell = row.createCell(j++);
                function.setCell(cell, t, workbook, this);
            }
        }
        workbook.write(stream);
        workbook.close();
    }

    @Override
    <S> Object convertDefault(S value) {
        return value;
    }

    public static class ListToStringConverter extends BaseConverter<List<?>, String> {

        private static final String SEPARATOR = "/";
        private final ConvertFactory<?> factory;

        public ListToStringConverter(ConvertFactory<?> factory) {
            super(List.class, String.class, false);
            this.factory = factory;
        }

        @Override
        public String convert(List<?> values) {
            List<String> strings = values.stream().map(value -> String.valueOf(factory.convertValue(value))).collect(Collectors.toList());
            return StringUtils.join(strings, SEPARATOR);
        }
    }

}
