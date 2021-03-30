package wsg.tools.internet.base.support;

import java.util.function.Function;
import javax.annotation.Nonnull;
import org.jsoup.nodes.Document;
import wsg.tools.internet.base.SnapshotStrategy;

/**
 * Update the snapshot when the document doesn't have the identifier of next entity.
 *
 * @param <ID> type of the identifier to be found in the document
 * @author Kingen
 * @since 2021/3/2
 */
public class WithoutNextDocument<ID> implements SnapshotStrategy<Document> {

    private final Function<Document, ID> getNext;

    public WithoutNextDocument(@Nonnull Function<Document, ID> getNext) {
        this.getNext = getNext;
    }

    @Override
    public boolean ifUpdate(Document document) {
        return getNext.apply(document) == null;
    }
}
