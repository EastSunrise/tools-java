package wsg.tools.internet.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.jetbrains.annotations.Contract;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.SiteStatus;
import wsg.tools.internet.base.page.PageReq;
import wsg.tools.internet.base.page.PageResult;
import wsg.tools.internet.base.repository.RepoPageable;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.view.SiblingSupplier;

/**
 * Utility for operations on sites.
 *
 * @author Kingen
 * @since 2021/3/1
 */
public final class SiteUtils {

    private static final char SEPARATOR = '.';
    private static final String[] COMMON_TOP_DOMAINS = {
        "com", "org", "net", "gov", "edu", "co", "top", "info"
    };
    private static final String[] REGION_TOP_DOMAINS = {
        "cn", "jp", "hk", "fr", "kr", "ru", "us", "uk"
    };
    private static final Pattern DOMAIN_REGEX = Pattern
        .compile("[a-z0-9][a-z0-9-]*[a-z0-9](\\.[a-z0-9][a-z0-9-]*[a-z0-9])+");

    private SiteUtils() {
    }

    /**
     * Finds a header of the specified name.
     */
    @Nonnull
    public static Header findHeader(@Nonnull Header[] headers, String name) {
        for (Header header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        throw new IllegalArgumentException("Can't find the header of " + name);
    }

    /**
     * Splits a {@link HttpResponseException}.
     *
     * @return other exception
     * @throws NotFoundException if the status code is 404
     */
    @Nonnull
    @Contract("_ -> new")
    public static OtherResponseException handleException(@Nonnull HttpResponseException e)
        throws NotFoundException {
        if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            throw new NotFoundException(e.getMessage());
        }
        return new OtherResponseException(e);
    }

    /**
     * Collects doubly-linked entities recursively.
     * <p>
     * Notes that the type of {@code ID} must override {@link Object#equals(Object)}, otherwise a
     * stack overflow exception may occurs.
     *
     * @param retrievable the function to retrieve an entity by an identifier
     * @param first       the identifier of the first entity
     * @param <ID>        type of identifiers that should override {@link #equals(Object)}
     * @param <T>         type of entities
     * @return a linked list of entities
     */
    @Nonnull
    public static <ID, T extends SiblingSupplier<ID>>
    List<T> collectSiblingEntities(RepoRetrievable<ID, T> retrievable, ID first)
        throws NotFoundException, OtherResponseException {
        List<T> entities = new LinkedList<>();
        find(first, retrievable, new HashSet<>(), entities);
        return entities;
    }

    private static <ID, T extends SiblingSupplier<ID>>
    void find(ID id, RepoRetrievable<ID, T> retrievable, Set<ID> ids, List<T> entities)
        throws NotFoundException, OtherResponseException {
        if (id == null) {
            return;
        }
        if (ids.contains(id)) {
            return;
        }
        T t = retrievable.findById(id);
        entities.add(t);
        ids.add(id);
        find(t.getNextId(), retrievable, ids, entities);
        find(t.getPreviousId(), retrievable, ids, entities);
    }

    /**
     * Traverses the pages in a pageable repository.
     *
     * @param pageable the function to retrieve a page by a given request with pagination
     *                 information
     * @param firstReq the first request with pagination information
     * @param action   action to be performed for the content of each page
     * @param <I>      type of the content of each page
     * @param <P>      type of requests with pagination information
     * @throws NotFoundException      if any page is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    public static <I, P extends PageReq> void
    forEachPage(@Nonnull RepoPageable<P, ? extends PageResult<I, P>> pageable, P firstReq,
        @Nonnull Consumer<List<I>> action) throws NotFoundException, OtherResponseException {
        P req = firstReq;
        while (true) {
            PageResult<I, P> result = pageable.findPage(req);
            action.accept(result.getContent());
            if (!result.hasNext()) {
                break;
            }
            req = result.nextPageRequest();
        }
    }

    /**
     * Traverses the pages in a pageable repository until an index matches the specified predicate.
     * This method requires that the indices found by page are ordered.
     *
     * @param pageable  the function to retrieve a page by a given request with pagination
     *                  information
     * @param firstReq  the first request with pagination information
     * @param action    action to be performed for content of each page
     * @param predicate predicate that ends the traversal
     * @param <I>       type of the content of each page
     * @param <P>       type of requests with pagination information
     * @throws NotFoundException      if any page is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    public static <I, P extends PageReq> void
    forEachPageUntil(@Nonnull RepoPageable<P, ? extends PageResult<I, P>> pageable, P firstReq,
        @Nonnull Consumer<I> action, @Nonnull Predicate<I> predicate)
        throws NotFoundException, OtherResponseException {
        P req = firstReq;
        boolean dead = false;
        while (true) {
            PageResult<I, P> result = pageable.findPage(req);
            for (I index : result.getContent()) {
                if (predicate.test(index)) {
                    dead = true;
                    break;
                }
                action.accept(index);
            }
            if (dead || !result.hasNext()) {
                break;
            }
            req = result.nextPageRequest();
        }
    }

    /**
     * Collects all indices by traversing the pages in a pageable repository.
     *
     * @param <I>      type of the content of each page
     * @param <P>      type of requests with pagination information
     * @param pageable the function to retrieve a page by a given request with pagination
     *                 information
     * @param firstReq the first request with pagination information
     * @return list of all indices found by pages
     * @throws NotFoundException      if any page is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    @Nonnull
    public static <I, P extends PageReq>
    List<I> collectPage(@Nonnull RepoPageable<P, ? extends PageResult<I, P>> pageable, P firstReq)
        throws NotFoundException, OtherResponseException {
        List<I> indices = new ArrayList<>();
        forEachPage(pageable, firstReq, indices::addAll);
        return indices;
    }

    /**
     * Splits the given domain.
     *
     * @param domain a full domain like 'xxx.xxx.xxx'
     * @return pair of main domain and sub domain if exists
     */
    @Nonnull
    public static Pair<String, String> splitDomain(String domain) {
        domain = AssertUtils.requireNotBlank(domain).toLowerCase(Locale.ENGLISH);
        RegexUtils.matchesOrElseThrow(DOMAIN_REGEX, domain);
        String[] parts = StringUtils.split(domain, SEPARATOR);
        int last = parts.length - 1;
        int main = last - 1;
        String top = parts[last];
        if (ArrayUtils.contains(REGION_TOP_DOMAINS, top)) {
            if (ArrayUtils.contains(COMMON_TOP_DOMAINS, parts[last - 1])) {
                main--;
                top = parts[last - 1] + SEPARATOR + top;
            }
        }
        if (main < 0) {
            throw new IllegalArgumentException("Not a valid domain: " + domain);
        }
        if (main == 0) {
            return Pair.of(parts[0] + SEPARATOR + top, null);
        }
        return Pair.of(parts[main] + SEPARATOR + top, StringUtils.join(parts, SEPARATOR, 0, main));
    }

    /**
     * Validate the status of the site based on the annotation {@link ConcreteSite}.
     *
     * @throws SiteStatusException if the status is abnormal
     */
    public static void validateStatus(@Nonnull Class<? extends BaseSite> clazz) {
        ConcreteSite annotation = clazz.getAnnotation(ConcreteSite.class);
        if (annotation != null) {
            SiteStatus status = annotation.status();
            if (SiteStatus.NORMAL != status) {
                throw new SiteStatusException(annotation);
            }
        }
    }
}
