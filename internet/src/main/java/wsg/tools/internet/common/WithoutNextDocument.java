package wsg.tools.internet.common;

import org.jsoup.nodes.Document;
import wsg.tools.internet.base.SnapshotStrategy;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Update the snapshot when the document doesn't have the index
 * of next record.
 *
 * @author Kingen
 * @see wsg.tools.internet.base.intf.IterableRepository
 * @see wsg.tools.internet.base.RecordIterator
 * @see wsg.tools.internet.base.NextSupplier
 * @since 2021/3/2
 */
public class WithoutNextDocument<T> implements SnapshotStrategy<Document> {

    private final Function<Document, T> getNext;

    public WithoutNextDocument(@Nonnull Function<Document, T> getNext) {this.getNext = getNext;}

    @Override
    public boolean ifUpdate(Document document) {
        return getNext.apply(document) == null;
    }
}
