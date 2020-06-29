package wsg.tools.common.entity;

import lombok.Getter;
import wsg.tools.common.jackson.intf.CodeSerializable;

/**
 * Class for money.
 *
 * @author Kingen
 * @since 2020/6/28
 */
@Getter
public class Money {

    private CurrencyEnum currency;
    private Double value;

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


    public enum CurrencyEnum implements CodeSerializable<String> {
        /**
         * RMB
         */
        YUAN("￥"),
        DOLLAR("$");

        private String sign;

        CurrencyEnum(String sign) {
            this.sign = sign;
        }

        @Override
        public String getCode() {
            return sign;
        }
    }
}
