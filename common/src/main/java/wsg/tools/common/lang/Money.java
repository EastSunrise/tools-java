package wsg.tools.common.lang;

import java.util.Objects;
import wsg.tools.common.util.function.AkaPredicate;

/**
 * Class for money.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class Money {

    private final CurrencyEnum currency;
    private final long value;

    public Money(String text) {
        this(parse(text));
    }

    public Money(Money other) {
        this(other.currency, other.value);
    }

    public Money(CurrencyEnum currency, long value) {
        this.currency = currency;
        this.value = value;
    }

    public static Money parse(String text) {
        Objects.requireNonNull(text, "Text can't be null.");
        for (Money.CurrencyEnum currency : Money.CurrencyEnum.values()) {
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

    public enum CurrencyEnum implements AkaPredicate<String> {
        /**
         * RMB
         */
        YUAN("CNY", "￥"), DOLLAR("USD", "$"), POUND("GBP", "£");

        private final String code;
        private final String sign;

        CurrencyEnum(String code, String sign) {
            this.code = code;
            this.sign = sign;
        }

        @Override
        public boolean alsoKnownAs(String other) {
            return code.equals(other) || sign.equals(other);
        }

        public String getCode() {
            return code;
        }

        public String getSign() {
            return sign;
        }
    }
}
