package wsg.tools.internet.base.support;

import java.util.function.Function;
import javax.annotation.Nonnull;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.apache.http.impl.execchain.ClientExecChain;
import org.jetbrains.annotations.Contract;

/**
 * The builder for {@link org.apache.http.impl.client.CloseableHttpClient} instances capable of
 * decorating the main executor.
 *
 * @author Kingen
 * @since 2021/5/10
 */
public class DecoratedHttpClientBuilder extends CachingHttpClientBuilder {

    private Function<ClientExecChain, ClientExecChain> decorator;

    protected DecoratedHttpClientBuilder() {
    }

    @Nonnull
    @Contract(" -> new")
    public static DecoratedHttpClientBuilder create() {
        return new DecoratedHttpClientBuilder();
    }

    @Override
    protected ClientExecChain decorateMainExec(ClientExecChain mainExec) {
        if (decorator != null) {
            ClientExecChain decoratedExec = decorator.apply(mainExec);
            if (decoratedExec != null) {
                return super.decorateMainExec(decoratedExec);
            }
        }
        return super.decorateMainExec(mainExec);
    }

    public DecoratedHttpClientBuilder setDecorator(
        Function<ClientExecChain, ClientExecChain> decorator) {
        this.decorator = decorator;
        return this;
    }
}
