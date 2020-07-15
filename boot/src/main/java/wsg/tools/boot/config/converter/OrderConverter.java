package wsg.tools.boot.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * Convert a string to an instance of {@link Order}.
 *
 * @author Kingen
 * @since 2020/7/14
 */
public class OrderConverter implements Converter<String, Order> {
    private static final String DELIMITER = ",";

    @Override
    public Order convert(String text) {
        String[] parts = text.split(DELIMITER);
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1) {
            direction = Sort.Direction.fromString(parts[1]);
        }
        return new Order(direction, parts[0]);
    }
}
