package wsg.tools.common.util.stream;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

/**
 * Utility to handle {@link Stream}
 *
 * @author Kingen
 * @since 2020/6/22
 */
@UtilityClass
public class StreamUtils {

    /**
     * Execute the given function recursively like traversing a directory.
     *
     * @param executable if current object is executable.
     * @param execution  consume current object if executable.
     * @param recursion  function to get children recursively.
     */
    public <F> void walk(F f, Predicate<F> executable, Function<F, Collection<F>> recursion,
        Consumer<F> execution) {
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
