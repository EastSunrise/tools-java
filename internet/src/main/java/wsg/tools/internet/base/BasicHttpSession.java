package wsg.tools.internet.base;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.common.Scheme;
import wsg.tools.internet.common.SiteUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Basic implementation of {@link HttpSession}.
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
@SuppressWarnings("UnstableApiUsage")
public class BasicHttpSession implements HttpSession, Closeable {

    private static final double DEFAULT_PERMITS_PER_SECOND = 10D;
    /**
     * temporary directory for snapshots and cookies.
     */
    private static final String TMPDIR = System.getProperty("java.io.tmpdir") + "tools";
    private static final Header USER_AGENT = new BasicHeader(HTTP.USER_AGENT,
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
    private static final int TIME_OUT = 30000;

    private final Scheme scheme;
    private final String mainDomain;
    private final String subDomain;
    private final HttpClientContext context;
    private final Map<String, RateLimiter> limiters;
    private final CloseableHttpClient client;
    private final ExecutorService executor =
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory());

    public BasicHttpSession(String domain) {
        this(domain, DEFAULT_PERMITS_PER_SECOND);
    }

    public BasicHttpSession(String domain, double permitsPerSecond) {
        this(Scheme.HTTPS, domain, permitsPerSecond, permitsPerSecond);
    }

    public BasicHttpSession(Scheme scheme, String domain) {
        this(scheme, domain, DEFAULT_PERMITS_PER_SECOND, DEFAULT_PERMITS_PER_SECOND);
    }

    public BasicHttpSession(Scheme scheme, String domain, double permitsPerSecond, double postPermitsPerSecond) {
        SiteUtils.validateStatus(this.getClass());
        this.scheme = scheme;
        Pair<String, String> pair = SiteUtils.splitDomain(domain);
        this.mainDomain = pair.getLeft();
        this.subDomain = "www".equals(pair.getRight()) ? null : pair.getRight();
        this.limiters = new HashMap<>(2);
        this.limiters.put(HttpGet.METHOD_NAME, RateLimiter.create(permitsPerSecond));
        this.limiters.put(HttpPost.METHOD_NAME, RateLimiter.create(postPermitsPerSecond));
        this.client = HttpClientBuilder.create().setDefaultHeaders(List.of(USER_AGENT, new BasicHeader(HTTP.TARGET_HOST, getHost())))
                .setConnectionManager(new PoolingHttpClientConnectionManager()).build();
        this.context = HttpClientContext.create();
        this.context.setRequestConfig(RequestConfig.custom().setConnectTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).build());
        File file = cookieFile();
        if (file.canRead()) {
            try (ObjectInputStream stream = new ObjectInputStream(FileUtils.openInputStream(file))) {
                log.info("Read cookies from {}.", file.getPath());
                CookieStore cookieStore = (CookieStore) stream.readObject();
                this.context.setCookieStore(cookieStore);
            } catch (IOException | ClassNotFoundException e) {
                throw AssertUtils.runtimeException(e);
            }
        }
    }

    @Override
    public final void close() throws IOException {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public final <T> T getContent(RequestBuilder builder, ResponseHandler<String> responseHandler, ContentHandler<T> contentHandler, SnapshotStrategy<T> strategy)
            throws HttpResponseException {
        String filepath = builder.filepath();
        filepath += Constants.FILE_EXTENSION_SEPARATOR + contentHandler.suffix();
        File file = new File(TMPDIR + filepath);

        String content;
        if (!file.isFile()) {
            return contentHandler.handleContent(updateSnapshot(builder, responseHandler, file));
        }
        log.info("Read from {}", file.getPath());
        try {
            content = FileUtils.readFileToString(file, UTF_8);
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
        T t = contentHandler.handleContent(content);
        if (!strategy.ifUpdate(t)) {
            return t;
        }
        return contentHandler.handleContent(updateSnapshot(builder, responseHandler, file));
    }

    /**
     * Executes the request, handle the response, return it as a String, and write to the file as a snapshot.
     */
    private String updateSnapshot(RequestBuilder builder, ResponseHandler<String> handler, File file) throws HttpResponseException {
        String content = execute(builder, handler);
        try {
            FileUtils.write(file, content, UTF_8);
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
        return content;
    }

    @Override
    public String getDomain() {
        return subDomain == null ? mainDomain : subDomain + "." + mainDomain;
    }

    public String getHost() {
        return (subDomain == null ? "www" : subDomain) + "." + mainDomain;
    }

    @Override
    public final <T> T execute(RequestBuilder builder, ResponseHandler<T> handler) throws HttpResponseException {
        log.info("{} from {}", builder.getMethod(), builder.displayUrl());
        limiters.get(builder.getMethod()).acquire();
        try {
            T entity = client.execute(builder.build(), handler, context);
            executor.execute(() -> {
                try (ObjectOutputStream stream = new ObjectOutputStream(FileUtils.openOutputStream(cookieFile()))) {
                    log.info("Synchronize cookies of {}.", getDomain());
                    stream.writeObject(context.getCookieStore());
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            });
            return entity;
        } catch (HttpResponseException e) {
            throw e;
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    private File cookieFile() {
        URIBuilder builder = new URIBuilder().setScheme(scheme.toString()).setHost(mainDomain);
        String filepath = new RequestBuilder(HttpGet.METHOD_NAME, builder).filepath();
        return new File(StringUtils.joinWith(File.separator, TMPDIR, "context", filepath + ".cookie"));
    }

    /**
     * Creates a builder to construct a request of the given method.
     */
    public RequestBuilder create(final String method) {
        AssertUtils.requireNotBlank(method);
        URIBuilder builder = new URIBuilder().setScheme(scheme.toString()).setHost(getHost());
        return new RequestBuilder(method, builder);
    }

    @Override
    public RequestBuilder create(final String method, final String subDomain) {
        AssertUtils.requireNotBlank(method);
        AssertUtils.requireNotBlank(subDomain);
        URIBuilder builder = new URIBuilder().setScheme(scheme.toString()).setHost(subDomain + "." + getDomain());
        return new RequestBuilder(method, builder);
    }

    @Override
    public Cookie getCookie(String name) {
        CookieStore cookieStore = this.context.getCookieStore();
        if (cookieStore == null) {
            return null;
        }
        for (Cookie cookie : cookieStore.getCookies()) {
            if (Objects.equals(cookie.getName(), name)) {
                return cookie;
            }
        }
        return null;
    }
}
