package wsg.tools.internet.base.support;

import java.util.function.Function;
import javax.annotation.Nonnull;
import org.jsoup.nodes.Document;
import wsg.tools.internet.base.SnapshotStrategy;

/**
 * Update the snapshot when the document doesn't have the index of next record.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public class WithoutNextDocument<T> implements SnapshotStrategy<Document> {

    private final Function<Document, T> getNext;

    public WithoutNextDocument(@Nonnull Function<Document, T> getNext) {
        this.getNext = getNext;
    }

    @Override
    public boolean ifUpdate(Document document) {
        return getNext.apply(document) == null;
    }
}
