package wsg.tools.common.lang.stream;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utility to handle {@link Stream}
 *
 * @author Kingen
 * @since 2020/6/22
 */
public class StreamUtils {

    /**
     * Find first element of the collection that match the given predicate.
     *
     * @return null if not exits
     */
    public static <T> T findFirst(Stream<T> ts, Predicate<? super T> predicate) {
        Iterator<T> iterator = ts.iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (predicate.test(t)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Execute the given function recursively like traversing a directory.
     *
     * @param executable if current object is executable.
     * @param execution  consume current object if executable.
     * @param recursion  function to get children recursively.
     */
    public static <F> void walk(F f, Predicate<F> executable, Function<F, Collection<F>> recursion, Consumer<F> execution) {
        if (executable.test(f)) {
            execution.accept(f);
        } else {
            Collection<F> children = recursion.apply(f);
            for (F child : children) {
                walk(child, executable, recursion, execution);
            }
        }
    }
}
