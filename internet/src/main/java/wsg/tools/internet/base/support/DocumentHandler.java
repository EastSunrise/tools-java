package wsg.tools.internet.base.support;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import wsg.tools.internet.base.ContentHandler;

/**
 * An implementation of {@code ContentHandler} to handle the html response and return as a
 * document.
 *
 * @author Kingen
 * @since 2021/3/31
 */
public class DocumentHandler implements ContentHandler<Document> {

    @Override
    public Document handleContent(String content) {
        return Jsoup.parse(content);
    }

    @Override
    public String extension() {
        return "html";
    }
}
