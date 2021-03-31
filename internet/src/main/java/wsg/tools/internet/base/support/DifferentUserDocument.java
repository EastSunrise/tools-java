package wsg.tools.internet.base.support;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.jsoup.nodes.Document;
import wsg.tools.internet.base.Loggable;
import wsg.tools.internet.base.SnapshotStrategy;

/**
 * An implementation of {@code SnapshotStrategy} to update the snapshot when the user in the
 * document is different from current user.
 *
 * @author Kingen
 * @since 2021/3/2
 */
class DifferentUserDocument<T> implements SnapshotStrategy<Document> {

    private final Loggable<T> loggable;
    private final Function<? super Document, T> getUser;

    DifferentUserDocument(@Nonnull Loggable<T> loggable,
        @Nonnull Function<? super Document, T> getUser) {
        this.loggable = loggable;
        this.getUser = getUser;
    }

    @Override
    public boolean ifUpdate(Document document) {
        return !Objects.equals(getUser.apply(document), loggable.user());
    }
}
