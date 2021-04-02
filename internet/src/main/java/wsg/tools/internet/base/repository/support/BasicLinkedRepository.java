package wsg.tools.internet.base.repository.support;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.repository.LinkedRepoIterator;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.RepoIterator;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * This class provides a skeletal implementation of {@code LinkedRepository} whose first entity is
 * mapped by {@link #first}, the first identifier. Another field {@link #next} supplies the method
 * to get the next identifier by current entity.
 *
 * @author Kingen
 * @since 2021/3/1
 */
class BasicLinkedRepository<ID, T> implements LinkedRepository<ID, T> {

    private final RepoRetrievable<ID, T> retrievable;
    private final ID first;
    private final Function<T, ID> next;

    BasicLinkedRepository(RepoRetrievable<ID, T> retrievable, ID first, Function<T, ID> next) {
        this.retrievable = Objects.requireNonNull(retrievable);
        this.first = first;
        this.next = Objects.requireNonNull(next);
    }

    @Override
    public boolean isEmpty() {
        return first == null;
    }

    @Override
    public ID firstIdentifier() {
        return first;
    }

    @Nonnull
    @Override
    public RepoIterator<T> repoIterator() {
        return new BasicLinkedRepoIterator<>(this, next, first);
    }

    @Nonnull
    @Override
    public LinkedRepoIterator<ID, T> linkedRepoIterator() {
        return new BasicLinkedRepoIterator<>(this, next, first);
    }

    @Nonnull
    @Override
    public LinkedRepoIterator<ID, T> linkedRepoIterator(ID id) {
        return new BasicLinkedRepoIterator<>(this, next, id);
    }

    @Nonnull
    @Override
    public T findById(@Nonnull ID id) throws NotFoundException, OtherResponseException {
        return retrievable.findById(id);
    }
}
