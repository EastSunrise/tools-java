package wsg.tools.internet.base.impl;

import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wsg.tools.internet.base.intf.ContentHandler;

/**
 * Common implementations of {@code ContentHandler}.
 *
 * @author Kingen
 * @since 2021/2/11
 */
@UtilityClass
public class ContentHandlers {

    /**
     * Parse the content to a {@link Document}.
     */
    public final ContentHandler<Document> DOCUMENT_CONTENT_HANDLER = new DocumentContentHandler();

    private class DocumentContentHandler implements ContentHandler<Document> {

        @Override
        public Document handleContent(String content) {
            return Jsoup.parse(content);
        }

        @Override
        public String suffix() {
            return "html";
        }
    }
}
