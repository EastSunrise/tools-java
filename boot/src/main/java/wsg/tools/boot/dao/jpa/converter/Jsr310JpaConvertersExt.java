package wsg.tools.boot.dao.jpa.converter;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Converter;
import java.time.Year;

/**
 * Extension of {@link Jsr310JpaConverters}
 *
 * @author Kingen
 * @since 2020/7/13
 */
public class Jsr310JpaConvertersExt {

    @Converter(autoApply = true)
    public static class YearConverter extends BaseNonNullConverter<Year, Integer> {

        @Override
        protected Year deserialize(Integer dbData) {
            return Year.of(dbData);
        }

        @Override
        protected Integer serialize(Year attribute) {
            return attribute.getValue();
        }
    }
}
