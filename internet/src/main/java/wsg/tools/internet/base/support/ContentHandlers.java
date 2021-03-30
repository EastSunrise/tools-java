package wsg.tools.internet.base.support;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wsg.tools.internet.base.ContentHandler;

/**
 * Common implementations of {@code ContentHandler}.
 *
 * @author Kingen
 * @since 2021/2/11
 */
public final class ContentHandlers {

    /**
     * Parse the content to a {@link Document}.
     */
    public static final ContentHandler<Document> DOCUMENT_CONTENT_HANDLER =
        new DocumentContentHandler();

    private ContentHandlers() {
    }

    private static class DocumentContentHandler implements ContentHandler<Document> {

        @Override
        public Document handleContent(String content) {
            return Jsoup.parse(content);
        }

        @Override
        public String extension() {
            return "html";
        }
    }
}