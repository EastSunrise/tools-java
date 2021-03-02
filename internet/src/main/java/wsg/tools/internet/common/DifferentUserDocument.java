package wsg.tools.internet.common;

import org.jsoup.nodes.Document;
import wsg.tools.internet.base.SnapshotStrategy;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;

/**
 * Update the snapshot when the user in the document is different from current user.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public class DifferentUserDocument<T> implements SnapshotStrategy<Document> {

    private final Loggable<T> loggable;
    private final Function<Document, T> getUser;

    public DifferentUserDocument(@Nonnull Loggable<T> loggable, @Nonnull Function<Document, T> getUser) {
        this.loggable = loggable;
        this.getUser = getUser;
    }

    @Override
    public boolean ifUpdate(Document document) {
        return !Objects.equals(getUser.apply(document), loggable.user());
    }
}
