package wsg.tools.internet.base.data.support;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.data.Descriptor;

/**
 * This class consists exclusively of static methods that provides common descriptors.
 *
 * @author Kingen
 * @since 2021/4/3
 */
@Slf4j
public final class Descriptors {

    private Descriptors() {
    }

    /**
     * Returns a descriptor to describe the range of the values.
     */
    @Nonnull
    @Contract(pure = true)
    public static <T extends Comparable<T>> Descriptor<T> range() {
        return values -> {
            if (!values.isEmpty()) {
                log.info("Min of the values: {}", values.stream().min(T::compareTo).orElseThrow());
                log.info("Max of the values: {}", values.stream().max(T::compareTo).orElseThrow());
            }
        };
    }

    /**
     * Returns a descriptor to enumerate distinct keys of the values.
     *
     * @param classifier the classifier function mapping values to keys
     */
    @Nonnull
    @Contract(pure = true)
    public static <T, K> Descriptor<T> enumerate(Function<T, K> classifier) {
        return values -> {
            Map<K, List<T>> map = values.stream().collect(Collectors.groupingBy(classifier));
            if (!map.isEmpty()) {
                log.info("Enumerating {} distinct keys...", map.size());
                for (Map.Entry<K, List<T>> entry : map.entrySet()) {
                    log.info("Key: {}, count: {}", entry.getKey(), entry.getValue().size());
                }
            }
        };
    }

    /**
     * Returns a descriptor to test whether each element of the values matches the predicate.
     */
    @Nonnull
    @Contract(pure = true)
    public static <T, K> Descriptor<T> test(Predicate<T> predicate, Function<T, K> identifier) {
        return values -> values.stream().filter(predicate).map(identifier)
            .forEach(id -> log.info("{}", id));
    }

    /**
     * Calculates the distribution of the given list of integer identifiers.
     *
     * @param step the step size to divide the list
     * @return the array of distribution, each element representing the count of ids in each part
     * divided by the step
     */
    @Nonnull
    public static int[] distribution(@Nonnull List<Integer> ids, int step) {
        int max = ids.stream().mapToInt(i -> i).max().orElseThrow();
        int[] array = new int[max + 1];
        for (Integer id : ids) {
            array[id] = 1;
        }
        int[] destinies = new int[max / step + 1];
        for (int i = 0; i < destinies.length; i++) {
            int start = i * step + 1;
            int end = Math.min(start + step, array.length);
            for (int j = start; j < end; j++) {
                destinies[i] += array[j];
            }
        }
        return destinies;
    }
}
