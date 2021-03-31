package wsg.tools.internet.base.support;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import org.jsoup.nodes.Document;
import wsg.tools.internet.base.ContentHandler;

/**
 * Common implementations of {@code ContentHandler}.
 *
 * @author Kingen
 * @since 2021/2/11
 */
public final class ContentHandlers {

    private ContentHandlers() {
    }

    @Nonnull
    @Contract(value = " -> new", pure = true)
    public static ContentHandler<Document> document() {
        return new DocumentContentHandler();
    }
}
