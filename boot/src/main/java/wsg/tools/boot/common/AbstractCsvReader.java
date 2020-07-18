package wsg.tools.boot.common;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Abstract class to read CSV File.
 * <p>
 * Implement this class to read different csv file.
 *
 * @author Kingen
 * @since 2020/7/10
 */
public abstract class AbstractCsvReader<T> {
    private static final String CSV_CONTENT_TYPE = "text/csv";
    protected String[] headers;

    public AbstractCsvReader(String[] headers) {
        this.headers = headers;
    }

    /**
     * Read list of data from a multipart file.
     *
     * @param file target file, not null
     * @return list of data
     */
    public List<T> readMultipartFile(MultipartFile file) throws IOException {
        return readMultipartFile(file, StandardCharsets.UTF_8);
    }

    /**
     * Read list of data from a multipart file with a specified charset.
     *
     * @param file    target file, not null
     * @param charset charset
     * @return list of data
     */
    public List<T> readMultipartFile(MultipartFile file, Charset charset) throws IOException {
        Objects.requireNonNull(file);
        if (!CSV_CONTENT_TYPE.equals(file.getContentType())) {
            throw new IllegalArgumentException("Not a CSV file");
        }
        if (file.isEmpty()) {
            return null;
        }
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers).withFirstRecordAsHeader();
        CSVParser parse = CSVParser.parse(file.getInputStream(), charset, csvFormat);
        List<CSVRecord> records = parse.getRecords();
        return records.stream().map(this::readRecord).filter(this::remain).collect(Collectors.toList());
    }

    /**
     * Filter elements.
     * <p>
     * Default to remain not-null elements.
     */
    protected boolean remain(T t) {
        return t != null;
    }

    /**
     * Obtains an object from the record
     *
     * @param record a record of a line
     * @return target object
     */
    protected abstract T readRecord(CSVRecord record);
}
