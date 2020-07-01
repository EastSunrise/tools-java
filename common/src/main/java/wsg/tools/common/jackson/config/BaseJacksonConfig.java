package wsg.tools.common.jackson.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import wsg.tools.common.jackson.deserializer.base.BaseNumberDeserializer;
import wsg.tools.common.jackson.deserializer.impl.*;
import wsg.tools.common.lang.Money;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;

/**
 * Base configuration for Jackson.
 *
 * @author Kingen
 * @since 2020/6/27
 */
public class BaseJacksonConfig {

    /**
     * Create a new instance of {@link ObjectMapper} with basic configurations.
     */
    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SimpleModule()
                .addDeserializer(LocalDate.class, new LocalDateDeserializer())
                .addDeserializer(LocalTime.class, new LocalTimeDeserializer())
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer())
                .addDeserializer(Year.class, new YearDeserializer())
                .addDeserializer(Long.class, BaseNumberDeserializer.LONG_DESERIALIZER)
                .addDeserializer(Money.class, new MoneyDeserializer())
        );
        return objectMapper;
    }
}
