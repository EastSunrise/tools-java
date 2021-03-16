package wsg.tools.internet.info.adult.common;

import java.util.Arrays;
import java.util.Locale;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * Enum for headers of serial numbers.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public enum SerialNumHeader implements IntCodeSupplier, AkaPredicate<String> {
    ;

    private final int code;
    private final String[] headers;

    SerialNumHeader(int code, String... headers) {
        this.code = code;
        this.headers = headers;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public boolean alsoKnownAs(String other) {
        String upperCase = other.toUpperCase(Locale.ROOT);
        return Arrays.stream(headers).map(s -> s.toUpperCase(Locale.ENGLISH))
            .anyMatch(s -> s.equals(upperCase));
    }

    public String[] getHeaders() {
        return headers;
    }
}
