package wsg.tools.internet.base.repository.support;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;

/**
 * Common repositories.
 *
 * @author Kingen
 * @since 2021/3/14
 */
public final class Repositories {

    private Repositories() {
    }

    /**
     * Constructs a linked repository starting with the given first identifier.
     *
     * @param retrievable the core function of the repository to retrieve an entity by an
     *                    identifier, must not be null
     * @param first       the first identifier in the repository, may be null if the repository is
     *                    empty
     */
    @Nonnull
    public static <ID, T extends NextSupplier<ID>> LinkedRepository<ID, T>
    linked(RepoRetrievable<ID, T> retrievable, ID first) {
        Objects.requireNonNull(retrievable);
        return new BasicLinkedRepository<>(retrievable, first, NextSupplier::nextId);
    }

    /**
     * Constructs a linked repository starting with the given first identifier.
     *
     * @param retrievable the core function of the repository to retrieve an entity by an
     *                    identifier, must not be null
     * @param next        the function to get next identifier by current entity, must not be null
     * @param first       the first identifier in the repository, may be null if the repository is
     *                    empty
     */
    @Nonnull
    public static <ID, T extends NextSupplier<ID>> LinkedRepository<ID, T>
    linked(RepoRetrievable<ID, T> retrievable, Function<T, ID> next, ID first) {
        Objects.requireNonNull(retrievable);
        Objects.requireNonNull(next);
        return new BasicLinkedRepository<>(retrievable, first, next);
    }

    /**
     * Constructs a list repository with a given list of identifiers.
     *
     * @param retrievable the core function of the repository to retrieve an entity by an
     *                    identifier, must not be null
     * @param ids         the list of all identifiers, must not be null but may empty
     */
    @Nonnull
    public static <ID, T> ListRepository<ID, T>
    list(RepoRetrievable<ID, T> retrievable, List<ID> ids) {
        Objects.requireNonNull(retrievable);
        Objects.requireNonNull(ids);
        return new BasicListRepository<>(retrievable, ids);
    }
}
