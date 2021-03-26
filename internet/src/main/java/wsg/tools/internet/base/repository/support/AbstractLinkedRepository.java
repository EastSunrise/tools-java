package wsg.tools.internet.base.repository.support;

import javax.annotation.Nonnull;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.base.repository.LinkedRepoIterator;
import wsg.tools.internet.base.repository.LinkedRepository;

/**
 * This class provides a skeletal implementation of {@link LinkedRepository} whose first entity is
 * mapped by {@link #first}, the first identifier.
 *
 * @param <T> type of entities which is required to implement {@link NextSupplier} to supply the
 *            identifier of next entity.
 * @author Kingen
 * @since 2021/3/1
 */
abstract class AbstractLinkedRepository<ID, T extends NextSupplier<ID>>
    implements LinkedRepository<ID, T> {

    private final ID first;

    AbstractLinkedRepository(ID first) {
        this.first = first;
    }

    @Override
    public boolean isEmpty() {
        return first == null;
    }

    @Override
    public ID firstIdentifier() {
        return first;
    }

    @Override
    public ID lastIdentifier() {
        throw new UnsupportedOperationException("Can't traverse in reverse direction");
    }

    @Nonnull
    @Override
    public LinkedRepoIterator<ID, T> repoIterator() {
        return new BasicLinkedRepoIterator<>(this, first);
    }

    @Nonnull
    @Override
    public LinkedRepoIterator<ID, T> linkedRepoIterator() {
        return new BasicLinkedRepoIterator<>(this, first);
    }

    @Nonnull
    @Override
    public LinkedRepoIterator<ID, T> linkedRepoIterator(ID id) {
        return new BasicLinkedRepoIterator<>(this, id);
    }
}
