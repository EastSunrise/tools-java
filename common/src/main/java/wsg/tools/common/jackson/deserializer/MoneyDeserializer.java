package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.Money;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserialize a string to {@link Money}.
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class MoneyDeserializer extends AbstractNonNullDeserializer<Money, String> {

    public static final MoneyDeserializer INSTANCE = new MoneyDeserializer();

    private static final Pattern MONEY_REGEX;

    static {
        StringBuilder builder = new StringBuilder();
        builder.append("([");
        Arrays.stream(Money.CurrencyEnum.values()).forEach(e -> builder.append(e.getCode()));
        builder.append("])(\\d+(,\\d{3})*)");
        MONEY_REGEX = Pattern.compile(builder.toString());
    }

    protected MoneyDeserializer() {
        super(Money.class, String.class);
    }

    @Override
    public Money apply(String text) {
        Matcher matcher = AssertUtils.matches(MONEY_REGEX, text);
        Money.CurrencyEnum currency = EnumUtilExt.deserializeCode(matcher.group(1), Money.CurrencyEnum.class);
        long value = Long.parseLong(matcher.group(2).replace(Constants.NUMBER_DELIMITER, ""));
        return new Money(currency, (double) value);
    }
}