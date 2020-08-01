package wsg.tools.boot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import wsg.tools.boot.config.hanler.ExcelDeserializationProblemHandlers;
import wsg.tools.boot.config.serializer.ContainerSerializers;
import wsg.tools.boot.pojo.enums.VideoTypeEnum;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.excel.ExcelFactory;
import wsg.tools.common.excel.reader.BaseCellToSetter;
import wsg.tools.common.excel.writer.BaseCellFromGetter;
import wsg.tools.common.function.CreatorSupplier;
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

    private static final ExcelFactory FACTORY = new ExcelFactory(new ObjectMapper()
            .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .registerModule(new SimpleModule()
                    .addSerializer(TitleSerializer.getInstance(VideoTypeEnum.class))
                    .addDeserializer(VideoTypeEnum.class, EnumDeserializers.getTitleDeserializer(VideoTypeEnum.class))
                    .addSerializer(TitleSerializer.getInstance(LanguageEnum.class))
                    .addDeserializer(LanguageEnum.class, EnumDeserializers.getTitleDeserializer(LanguageEnum.class))
                    .addSerializer(ContainerSerializers.CollectionToStringSerializer.getInstance(Constants.SLASH_DELIMITER))
            ).registerModule(new JavaTimeModule())
            .addHandler(SeparatedValueDeserializationProblemHandler.getInstance(Constants.SLASH_DELIMITER))
            .addHandler(ExcelDeserializationProblemHandlers.FloatToYearDeserializationProblemHandler.INSTANCE)
    );

    /**
     * Read an imported .csv file.
     *
     * @see ExcelFactory#readCsv(InputStream, Map, CreatorSupplier, Charset)
     */
    protected <T> List<T> readCsv(MultipartFile file, Map<String, BaseCellToSetter<T, ?>> readers, CreatorSupplier<T> creator, Charset charset) throws IOException {
        return FACTORY.readCsv(getInputStream(file, ContentType.CSV), readers, creator, charset);
    }

    /**
     * Export a .csv file.
     *
     * @see ExcelFactory#writeCsv(Appendable, List, LinkedHashMap)
     */
    protected <T> void exportCsv(HttpServletResponse response, List<T> data, String filename,
                                 LinkedHashMap<String, BaseCellFromGetter<T, ?>> writers) throws IOException {
        PrintWriter writer = new PrintWriter(getOutputStream(response, filename, ContentType.CSV), true, Constants.UTF_8);
        FACTORY.writeCsv(writer, data, writers);
        writer.close();
    }

    /**
     * Read an imported .xlsx file.
     *
     * @see ExcelFactory#readXlsx(InputStream, Map, CreatorSupplier)
     */
    protected <T> List<T> readXlsx(MultipartFile file, Map<String, BaseCellToSetter<T, ?>> readers, CreatorSupplier<T> creator) throws IOException {
        return FACTORY.readXlsx(getInputStream(file, ContentType.XLSX), readers, creator);
    }

    /**
     * Export an .xlsx file.
     *
     * @see ExcelFactory#writeXlsx(OutputStream, List, LinkedHashMap)
     */
    protected <T> void exportXlsx(HttpServletResponse response, List<T> data, String filename,
                                  LinkedHashMap<String, BaseCellFromGetter<T, ?>> writers) throws IOException {
        FACTORY.writeXlsx(getOutputStream(response, filename, ContentType.XLSX), data, writers);
    }

    private InputStream getInputStream(MultipartFile file, ContentType contentType) throws IOException {
        Objects.requireNonNull(file);
        if (!contentType.text.equals(file.getContentType())) {
            throw new IllegalArgumentException("Not a file of " + contentType);
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("The file is empty");
        }
        return file.getInputStream();
    }

    private OutputStream getOutputStream(HttpServletResponse response, String filename, ContentType contentType) throws IOException {
        if (StringUtils.isBlank(filename)) {
            filename = "example";
        } else {
            filename = URLEncoder.encode(filename, Constants.UTF_8);
        }
        response.setContentType(contentType.text);
        response.setCharacterEncoding(Constants.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + contentType.suffix);
        return response.getOutputStream();
    }

    enum ContentType {
        /**
         * CSV/XLSX
         */
        CSV("text/csv", ".csv"),
        XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx");

        final String text;
        final String suffix;

        ContentType(String text, String suffix) {
            this.text = text;
            this.suffix = suffix;
        }
    }
}
