package wsg.tools.internet.base.data.support.util;

import java.net.URL;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import wsg.tools.common.net.NetUtils;
import wsg.tools.internet.base.data.Validator;
import wsg.tools.internet.base.data.support.Descriptors;
import wsg.tools.internet.base.data.support.InvalidValueException;

/**
 * Validates whether the values are valid URLs.
 *
 * @author Kingen
 * @since 2021/3/31
 */
@Slf4j
public class URLValidator extends Validator<URL> {

    @Nonnull
    @Override
    public URL convert(@Nonnull Object value) throws InvalidValueException {
        if (value instanceof URL) {
            return (URL) value;
        }
        try {
            return NetUtils.createURL(value.toString());
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(e.getMessage());
        }
    }

    /**
     * Groups the urls by host.
     */
    @Override
    public void describe(@Nonnull List<URL> urls) {
        Descriptors.enumerate(urls, url -> {
            StringBuilder builder = new StringBuilder(url.toString().length())
                .append(url.getProtocol()).append("://")
                .append(url.getHost());
            if (-1 != url.getPort()) {
                builder.append(":").append(url.getPort());
            }
            return builder.toString();
        });
    }
}
