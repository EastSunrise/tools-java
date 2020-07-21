package wsg.tools.common.lang;

import lombok.Getter;
import wsg.tools.common.jackson.intf.CodeSupplier;

/**
 * Class for money.
 *
 * @author Kingen
 * @since 2020/6/28
 */
@Getter
public class Money {

    private final CurrencyEnum currency;
    private final Double value;

    public Money(CurrencyEnum currency, Double value) {
        this.currency = currency;
        this.value = value;
    }

    public Money of(CurrencyEnum currency, Double value) {
        return new Money(currency, value);
    }

    public Money of(CurrencyEnum currency, Long value) {
        return new Money(currency, value.doubleValue());
    }


    public enum CurrencyEnum implements CodeSupplier<String> {
        /**
         * RMB
         */
        YUAN("￥"),
        DOLLAR("$");

        private final String sign;

        CurrencyEnum(String sign) {
            this.sign = sign;
        }

        @Override
        public String getCode() {
            return sign;
        }
    }
}
