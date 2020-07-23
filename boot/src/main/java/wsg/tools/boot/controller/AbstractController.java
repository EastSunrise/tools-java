package wsg.tools.boot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import wsg.tools.boot.config.serializer.ContainerSerializers;
import wsg.tools.boot.pojo.enums.TypeEnum;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.excel.ExcelFactory;
import wsg.tools.common.excel.reader.CellReader;
import wsg.tools.common.excel.writer.CellWriter;
import wsg.tools.common.excel.writer.ValueSupplier;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.jackson.serializer.TitleSerializer;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.jackson.handler.SeparatedValueDeserializationProblemHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
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
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final ExcelFactory FACTORY = new ExcelFactory(new ObjectMapper()
            .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .registerModule(new SimpleModule()
                    .addSerializer(TitleSerializer.getInstance(TypeEnum.class))
                    .addDeserializer(TypeEnum.class, EnumDeserializers.getTitleDeserializer(TypeEnum.class))
                    .addSerializer(TitleSerializer.getInstance(LanguageEnum.class))
                    .addDeserializer(LanguageEnum.class, EnumDeserializers.getTitleDeserializer(LanguageEnum.class))
                    .addSerializer(ContainerSerializers.CollectionToStringSerializer.getInstance(Constants.SLASH_DELIMITER))
            ).registerModule(new JavaTimeModule())
            .addHandler(SeparatedValueDeserializationProblemHandler.getInstance(Constants.SLASH_DELIMITER))
    );

    /**
     * Read an imported .csv file encoded with utf-8.
     */
    protected List<Map<String, ?>> readCsv(MultipartFile file, Map<String, Class<?>> classes) throws IOException {
        return readCsv(file, classes, UTF_8);
    }

    /**
     * Read an imported .csv file.
     *
     * @see ExcelFactory#readCsv(InputStream, Map, Charset)
     */
    protected List<Map<String, ?>> readCsv(MultipartFile file, Map<String, Class<?>> classes, Charset charset) throws IOException {
        Objects.requireNonNull(file);
        if (!CSV_CONTENT_TYPE.equals(file.getContentType())) {
            throw new IllegalArgumentException("Not a CSV file");
        }
        if (file.isEmpty()) {
            return null;
        }
        return FACTORY.readCsv(file.getInputStream(), classes, charset);
    }

    /**
     * Export a .csv file.
     *
     * @see ExcelFactory#writeCsv(Appendable, List, LinkedHashMap)
     */
    protected <T> void exportCsv(HttpServletResponse response, List<T> data, String filename,
                                 LinkedHashMap<String, ValueSupplier<T, ?>> suppliers) throws IOException {
        Objects.requireNonNull(data);
        Objects.requireNonNull(suppliers);
        if (StringUtils.isBlank(filename)) {
            filename = "example";
        } else {
            filename = URLEncoder.encode(filename, UTF_8);
        }
        response.setContentType(CSV_CONTENT_TYPE);
        response.setCharacterEncoding(UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".csv");
        PrintWriter writer = new PrintWriter(response.getOutputStream(), true, UTF_8);
        FACTORY.writeCsv(writer, data, suppliers);
        writer.close();
    }

    /**
     * Read an imported .xlsx file.
     *
     * @see ExcelFactory#readXlsx(InputStream, Map)
     */
    protected List<Map<String, ?>> readXlsx(MultipartFile file, Map<String, CellReader<?>> readers) throws IOException {
        Objects.requireNonNull(file);
        if (!XLSX_CONTENT_TYPE.equals(file.getContentType())) {
            throw new IllegalArgumentException("Not an .xlsx file");
        }
        if (file.isEmpty()) {
            return null;
        }
        return FACTORY.readXlsx(file.getInputStream(), readers);
    }

    /**
     * Export an .xlsx file.
     *
     * @see ExcelFactory#writeXlsx(OutputStream, List, LinkedHashMap)
     */
    protected <T> void exportXlsx(HttpServletResponse response, List<T> data, String filename,
                                  LinkedHashMap<String, CellWriter<T, ?>> writers) throws IOException {
        if (StringUtils.isBlank(filename)) {
            filename = "example";
        } else {
            filename = URLEncoder.encode(filename, UTF_8);
        }
        response.setContentType(XLSX_CONTENT_TYPE);
        response.setCharacterEncoding(UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xlsx");
        FACTORY.writeXlsx(response.getOutputStream(), data, writers);
    }
}
