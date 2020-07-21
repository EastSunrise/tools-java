package wsg.tools.boot.controller;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import wsg.tools.common.converter.ExcelFactory;
import wsg.tools.common.converter.base.Converter;
import wsg.tools.common.converter.impl.JavaTimeConverters;
import wsg.tools.common.converter.impl.TitleConverter;
import wsg.tools.common.excel.CellEditable;
import wsg.tools.common.excel.ValueSupplier;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Base controller
 *
 * @author Kingen
 * @since 2020/6/22
 */
public abstract class AbstractController {

    private static final String CSV_CONTENT_TYPE = "text/csv";
    private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final ExcelFactory FACTORY = ExcelFactory.getInstance();

    static {
        FACTORY.putConverter(TitleConverter.INSTANCE);
        JavaTimeConverters.getConverters().forEach(FACTORY::putConverter);
        FACTORY.putConverter(JavaTimeConverters.DurationToLongConverter.DURATION_TO_MINUTES_CONVERTER);
        FACTORY.putConverter(new ExcelFactory.ListToStringConverter(FACTORY));
    }

    /**
     * Read a imported csv file encoded with utf-8.
     *
     * @param file     imported file
     * @param function converter from {@link CSVRecord} to target object
     * @param header   headers of table
     * @return list of objects
     */
    protected <T> List<T> readCsv(MultipartFile file, Converter<CSVRecord, T> function, String... header) throws IOException {
        return readCsv(file, function, UTF_8, header);
    }

    /**
     * Read a imported csv file.
     *
     * @param file     imported file
     * @param function converter from {@link CSVRecord} to target object
     * @param charset  given charset
     * @param header   headers of table
     * @return list of objects
     */
    protected <T> List<T> readCsv(MultipartFile file, Converter<CSVRecord, T> function, Charset charset, String... header) throws IOException {
        Objects.requireNonNull(file);
        if (!CSV_CONTENT_TYPE.equals(file.getContentType())) {
            throw new IllegalArgumentException("Not a CSV file");
        }
        if (file.isEmpty()) {
            return null;
        }
        return FACTORY.readCsv(file.getInputStream(), function, charset, header);
    }

    /**
     * Export a csv file.
     *
     * @param data      data to write to the csv file
     * @param filename  name of the exported file
     * @param functions functions to get values
     */
    protected <T> void exportCsv(HttpServletResponse response, List<T> data, String filename,
                                 Map<String, ValueSupplier<T, ?>> functions) throws IOException {
        Objects.requireNonNull(data);
        Objects.requireNonNull(functions);
        if (StringUtils.isBlank(filename)) {
            filename = "example";
        } else {
            filename = URLEncoder.encode(filename, UTF_8);
        }
        response.setContentType(CSV_CONTENT_TYPE);
        response.setCharacterEncoding(UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".csv");
        PrintWriter writer = new PrintWriter(response.getOutputStream(), true, UTF_8);
        FACTORY.exportCsv(writer, data, functions);
        writer.close();
    }

    protected <T> void exportXlsx(HttpServletResponse response, List<T> data, String filename,
                                  Map<String, CellEditable<T, ?>> functions) throws IOException {
        if (StringUtils.isBlank(filename)) {
            filename = "example";
        } else {
            filename = URLEncoder.encode(filename, UTF_8);
        }
        response.setContentType(XLSX_CONTENT_TYPE);
        response.setCharacterEncoding(UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xlsx");
        FACTORY.exportXlsx(response.getOutputStream(), data, functions);
    }
}
