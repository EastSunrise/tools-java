package wsg.tools.internet.base.data.support;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import wsg.tools.common.net.NetUtils;
import wsg.tools.internet.base.data.InvalidValueException;

/**
 * Validates whether the values are valid URLs.
 *
 * @author Kingen
 * @since 2021/3/31
 */
@Slf4j
public class URLValidator extends AbstractValidator<URL> {

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
        Set<String> homes = urls.stream().map(url -> {
            StringBuilder builder = new StringBuilder()
                .append(url.getProtocol()).append("://")
                .append(url.getHost());
            if (url.getPort() != -1) {
                builder.append(":").append(url.getPort());
            }
            return builder.toString();
        }).collect(Collectors.toSet());
        log.info("Homes: {}", homes.size());
        homes.forEach(log::info);
    }
}
