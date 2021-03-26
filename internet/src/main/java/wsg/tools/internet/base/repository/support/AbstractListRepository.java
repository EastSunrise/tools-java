package wsg.tools.internet.base.repository.support;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.repository.ListRepoIterator;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.RepoIterator;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * This class provides a skeletal implementation of {@link ListRepository} which maps an
 * unmodifiable list of identifiers to the entities.
 *
 * @author Kingen
 * @since 2021/3/15
 */
abstract class AbstractListRepository<ID, T> implements ListRepository<ID, T> {

    private final List<ID> identifiers;

    AbstractListRepository(List<ID> identifiers) {
        this.identifiers = Collections.unmodifiableList(Objects.requireNonNull(identifiers));
    }

    @Override
    public boolean isEmpty() {
        return identifiers.isEmpty();
    }

    @Override
    public int size() {
        return identifiers.size();
    }

    @Override
    public boolean containsIdentifier(ID id) {
        return identifiers.contains(id);
    }

    @Nonnull
    @Override
    public RepoIterator<T> repoIterator() {
        return new BasicListRepoIterator<>(this, identifiers.listIterator());
    }

    @Nonnull
    @Override
    public ID getIdentifier(int index) {
        return identifiers.get(index);
    }

    @Override
    public int indexOf(ID id) {
        return identifiers.indexOf(id);
    }

    @Override
    public int lastIndexOf(ID id) {
        return identifiers.lastIndexOf(id);
    }

    @Nonnull
    @Override
    public ListRepoIterator<ID, T> listRepoIterator() {
        return new BasicListRepoIterator<>(this, identifiers.listIterator());
    }

    @Nonnull
    @Override
    public ListRepoIterator<ID, T> listRepoIterator(int index) {
        return new BasicListRepoIterator<>(this, identifiers.listIterator(index));
    }

    @Nonnull
    @Override
    public ListRepository<ID, T> subRepository(int fromInclusive, int toExclusive) {
        return new AbstractListRepository<>(identifiers.subList(fromInclusive, toExclusive)) {
            @Nonnull
            @Override
            public T findById(ID id) throws NotFoundException, OtherResponseException {
                return AbstractListRepository.this.findById(id);
            }
        };
    }

    @Nonnull
    @Override
    public List<ID> indices() {
        return identifiers;
    }
}
