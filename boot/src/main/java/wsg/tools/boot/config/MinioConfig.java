package wsg.tools.boot.config;

import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.NoResponseException;
import io.minio.policy.PolicyType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.internet.common.OtherHttpResponseException;

/**
 * Configurations for MinIO.
 *
 * @author Kingen
 * @see <a href="https://docs.min.io/cn/minio-quickstart-guide.html">MinIO Quickstart Guide</a>
 * @since 2021/3/23
 */
@Slf4j
@Component
@PropertySource("classpath:config/minio.properties")
public class MinioConfig implements InitializingBean {

    private static final MinioBucket BUCKET_COVER = new MinioBucket("covers")
        .setPolicyAll(PolicyType.READ_ONLY);
    private static final MinioBucket BUCKET_ADULT = new MinioBucket("adult")
        .setPolicyAll(PolicyType.READ_ONLY);
    private static final String CONNECTION_REFUSED = "Connection refused: connect";
    private final File tmpdir;
    private final SiteManager manager;

    @Value("${endpoint}")
    private String endpoint;
    @Value("${access}")
    private String accessKey;
    @Value("${secret}")
    private String secretKey;

    private MinioClient minioClient;

    @Autowired
    public MinioConfig(PathConfiguration pathConfiguration, SiteManager manager) {
        this.tmpdir = pathConfiguration.tmpdir(BUCKET_COVER.getName());
        this.manager = manager;
    }

    /**
     * Uploads a cover of the given url
     *
     * @param url    url pointing to the cover, not null
     * @param source the source of the cover, not null
     * @throws NotFoundException          if not found
     * @throws OtherHttpResponseException if an unexpected {@code HttpResponseException} occurs
     */
    public String uploadCover(URL url, Source source)
        throws NotFoundException, OtherHttpResponseException {
        Objects.requireNonNull(url, "the url of the cover");
        Objects.requireNonNull(source, "the source of the cover");
        File file = download(url);
        String folder = source.getDomain() + Constants.URL_PATH_SEPARATOR + source.getSubtype();
        return uploadLocal(file, BUCKET_COVER, folder, String.valueOf(source.getRid()));
    }

    public String uploadEntryImage(URL url, String code)
        throws NotFoundException, OtherHttpResponseException {
        Objects.requireNonNull(url, "the url of the image");
        Objects.requireNonNull(code, "the code of the entry");
        File file = download(url);
        return uploadLocal(file, BUCKET_ADULT, code, UUID.randomUUID().toString());
    }

    /**
     * Uploads a local file to the given folder under the bucket.
     *
     * @see #uploadLocal(File, MinioBucket, String, String)
     */
    public String uploadLocal(File file, MinioBucket bucket, String folder) {
        return uploadLocal(file, bucket, folder, null);
    }

    /**
     * Uploads a local file to the given folder under the bucket with a specific filename.
     *
     * @param file     the file to upload, must be not null
     * @param folder   the folder that the file is uploaded to, may null
     * @param basename specify a basename for the file to upload, using the basename of the source
     *                 file if null
     * @return the url to access the uploaded file
     * @throws NullPointerException     if the file or bucket is null
     * @throws IllegalArgumentException if the file can't read
     * @throws AppException             if an error occurs when uploading
     */
    public String uploadLocal(File file, MinioBucket bucket, String folder, String basename) {
        Objects.requireNonNull(file, "the file to upload must not be null");
        if (!file.canRead()) {
            throw new IllegalArgumentException("the file to upload can't read");
        }
        Objects.requireNonNull(bucket, "the bucket to upload to");
        String filename = file.getName();
        if (basename != null) {
            String extension = FilenameUtils.getExtension(filename);
            if (!extension.isEmpty()) {
                extension = FilenameUtils.EXTENSION_SEPARATOR + extension;
            }
            filename = basename + extension;
        }
        String target = filename;
        if (folder != null) {
            target = folder + Constants.URL_PATH_SEPARATOR + target;
        }
        try {
            client().putObject(bucket.getName(), target, file.getPath());
            log.info("Uploaded {} to {}/{}", file, bucket, target);
            return client().getObjectUrl(bucket.getName(), target);
        } catch (InvalidBucketNameException | XmlPullParserException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException e) {
            throw new AppException(e);
        }
    }

    private File download(URL url) throws NotFoundException, OtherHttpResponseException {
        try {
            String file = StringUtils.stripEnd(url.getFile(), Constants.URL_PATH_SEPARATOR);
            String path = FilenameUtils.getPath(StringUtilsExt.toFilename(file));
            return manager.downloader().download(new File(tmpdir, path), url);
        } catch (ConnectException e) {
            if (CONNECTION_REFUSED.equals(e.getMessage())) {
                throw new OtherHttpResponseException(HttpStatus.SC_FORBIDDEN, CONNECTION_REFUSED);
            }
            throw new AppException(e);
        } catch (UnknownHostException e) {
            throw new OtherHttpResponseException(HttpStatus.SC_BAD_REQUEST, e.getMessage());
        } catch (FileNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (SocketTimeoutException e) {
            throw new OtherHttpResponseException(HttpStatus.SC_REQUEST_TIMEOUT, e.getMessage());
        } catch (SSLException | SocketException e) {
            String message = e.getMessage();
            throw new OtherHttpResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR, message);
        } catch (IOException e) {
            Matcher matcher = Lazy.RESPONSE_EXCEPTION_REGEX.matcher(e.getMessage());
            if (matcher.lookingAt()) {
                int code = Integer.parseInt(matcher.group("c"));
                throw new OtherHttpResponseException(code, e.getMessage());
            }
            throw new AppException(e);
        }
    }

    public MinioClient client() {
        if (minioClient == null) {
            try {
                minioClient = new MinioClient(endpoint, accessKey, secretKey);
            } catch (InvalidEndpointException | InvalidPortException e) {
                throw new AppException(e);
            }
        }
        return minioClient;
    }

    @Override
    public void afterPropertiesSet()
        throws InvalidBucketNameException, NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, NoResponseException, XmlPullParserException, ErrorResponseException, InternalException, io.minio.errors.RegionConflictException, io.minio.errors.InvalidObjectPrefixException {
        MinioClient client = client();
        List<MinioBucket> buckets = List.of(BUCKET_COVER);
        for (MinioBucket bucket : buckets) {
            String name = bucket.getName();
            if (!client.bucketExists(name)) {
                client.makeBucket(name);
            }
            for (Map.Entry<String, PolicyType> entry : bucket.getPolicies().entrySet()) {
                client.setBucketPolicy(name, entry.getKey(), entry.getValue());
            }
        }
    }

    private static class Lazy {

        private static final Pattern RESPONSE_EXCEPTION_REGEX = Pattern
            .compile("Server returned HTTP response code: (?<c>[45]\\d{2})");
    }
}
