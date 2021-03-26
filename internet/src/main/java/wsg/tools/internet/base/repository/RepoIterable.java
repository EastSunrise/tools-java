package wsg.tools.internet.base.repository;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * A variant of {@link Iterable} for {@link Repository}.
 *
 * @param <T> the type of entities returned by the iterator
 * @author Kingen
 * @since 2021/3/26
 */
public interface RepoIterable<T> {

    /**
     * Returns an iterator over entities of type {@code T}.
     *
     * @return the iterator
     */
    @Nonnull
    RepoIterator<T> repoIterator();

    /**
     * Performs the given action for each entity in the repository until all entity have been
     * processed or the action throws an unexpected exception.
     *
     * @param action         the action to be performed for each entity
     * @param notFoundAction the action to be performer if the entity is not found
     * @param failAction     the action to be performed if an unexpected error occurs when
     *                       requesting
     * @throws NullPointerException if any of the specified actions is null
     */
    default void forEach(Consumer<? super T> action, Consumer<NotFoundException> notFoundAction,
        Consumer<OtherResponseException> failAction) {
        Objects.requireNonNull(action);
        Objects.requireNonNull(notFoundAction);
        Objects.requireNonNull(failAction);
        RepoIterator<T> iterator = repoIterator();
        while (iterator.hasNext()) {
            try {
                action.accept(iterator.next());
            } catch (NotFoundException e) {
                notFoundAction.accept(e);
            } catch (OtherResponseException e) {
                failAction.accept(e);
            }
        }
    }
}
