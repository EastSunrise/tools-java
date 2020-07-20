package wsg.tools.boot.controller;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import wsg.tools.boot.config.serializer.CsvConverters;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base controller
 *
 * @author Kingen
 * @since 2020/6/22
 */
public abstract class AbstractController {

    private static final String CSV_CONTENT_TYPE = "text/csv";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final CSVFormat FORMAT = CSVFormat.DEFAULT.withQuote(null);

    /**
     * Read a imported csv file encoded with utf-8.
     *
     * @param file     imported file
     * @param function converter from {@link CSVRecord} to target object
     * @param header   headers of table
     * @return list of objects
     */
    protected <T> List<T> readCsv(MultipartFile file, Function<CSVRecord, T> function, String... header) throws IOException {
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
    protected <T> List<T> readCsv(MultipartFile file, Function<CSVRecord, T> function, Charset charset, String... header) throws IOException {
        Objects.requireNonNull(file);
        if (!CSV_CONTENT_TYPE.equals(file.getContentType())) {
            throw new IllegalArgumentException("Not a CSV file");
        }
        if (file.isEmpty()) {
            return null;
        }
        CSVFormat format = FORMAT;
        if (ArrayUtils.isNotEmpty(header)) {
            format = format.withHeader(header).withFirstRecordAsHeader();
        }
        CSVParser parse = CSVParser.parse(file.getInputStream(), charset, format);
        List<CSVRecord> records = parse.getRecords();
        return records.stream().map(function).collect(Collectors.toList());
    }

    /**
     * Export a csv file.
     *
     * @param data      data to write to the csv file
     * @param filename  name of the exported file
     * @param functions functions to handle each column
     */
    protected <T> void exportCsv(HttpServletResponse response, List<T> data, String filename,
                                 Map<String, Function<T, ?>> functions) throws IOException {
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
        CSVPrinter printer = FORMAT.print(writer);
        printer.printRecord(functions.keySet());
        for (T t : data) {
            for (Function<T, ?> function : functions.values()) {
                printer.print(CsvConverters.convert(function.apply(t)));
            }
            printer.println();
        }
        printer.close(true);
        writer.close();
    }
}
