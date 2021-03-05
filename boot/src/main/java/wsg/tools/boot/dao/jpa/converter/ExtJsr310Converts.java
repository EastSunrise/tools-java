package wsg.tools.boot.dao.jpa.converter;

import java.time.Duration;
import javax.persistence.Converter;

/**
 * Extension of {@link org.springframework.data.convert.Jsr310Converters}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
public final class ExtJsr310Converts {

    @Converter(autoApply = true)
    public static class DurationToLongConverter extends BaseNonNullConverter<Duration, Integer> {

        @Override
        protected Duration deserialize(Integer dbData) {
            return Duration.ofMinutes(dbData);
        }

        @Override
        protected Integer serialize(Duration attribute) {
            return Math.toIntExact(attribute.toMinutes());
        }
    }
}
