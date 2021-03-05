package wsg.tools.internet.base.impl;

import java.util.function.Function;
import javax.annotation.Nonnull;
import org.jsoup.nodes.Document;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.common.NextSupplier;

/**
 * Update the snapshot when the document doesn't have the index of next record.
 *
 * @author Kingen
 * @see wsg.tools.internet.base.intf.IterableRepository
 * @see wsg.tools.internet.base.intf.RepositoryIterator
 * @see NextSupplier
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
