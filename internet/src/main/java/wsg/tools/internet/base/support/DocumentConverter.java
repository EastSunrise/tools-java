package wsg.tools.internet.base.support;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wsg.tools.internet.base.ContentConverter;

/**
 * An implementation of {@code ContentHandler} to convert the html response to a {@link Document}.
 *
 * @author Kingen
 * @since 2021/3/31
 */
public class DocumentConverter implements ContentConverter<Document> {

    @Override
    public Document convert(String content) {
        return Jsoup.parse(content);
    }
}
