package wsg.tools.common.util;

import java.util.Iterator;
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
}
