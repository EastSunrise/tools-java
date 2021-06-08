package wsg.tools.common.lang;

import java.util.Objects;

/**
 * Class for money.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class Money {

    private final Currency currency;
    private final long value;

    public Money(String text) {
        this(parse(text));
    }

    public Money(Money other) {
        this(other.currency, other.value);
    }

    public Money(Currency currency, long value) {
        this.currency = currency;
        this.value = value;
    }

    public static Money parse(String text) {
        Objects.requireNonNull(text, "Text can't be null.");
        for (Currency currency : Currency.values()) {
            String value;
            if (text.startsWith(currency.getCode())) {
                value = text.substring(currency.getCode().length());
            } else if (text.startsWith(currency.getSign())) {
                value = text.substring(currency.getSign().length());
            } else {
                continue;
            }
            return new Money(currency, NumberUtilsExt.parseCommaSeparatedNumber(value));
        }
        throw new IllegalArgumentException("Can't parse money from '" + text + "'.");
    }

    @Override
    public String toString() {
        return "Money{" +
            "currency=" + currency +
            ", value=" + value +
            '}';
    }

    public enum Currency {
        /**
         * RMB
         */
        YUAN("CNY", "￥"),
        DOLLAR("USD", "$"),
        POUND("GBP", "£");

        private final String code;
        private final String sign;

        Currency(String code, String sign) {
            this.code = code;
            this.sign = sign;
        }

        public String getCode() {
            return code;
        }

        public String getSign() {
            return sign;
        }
    }
}
