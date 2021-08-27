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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.net.ssl.SSLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;
import wsg.tools.boot.dao.api.support.SiteManager;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.common.Constants;
import wsg.tools.common.io.FileUtilExt;
import wsg.tools.common.io.Filetype;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.SiteUtils;

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

    private static final MinioBucket[] BUCKETS = {
        new MinioBucket("covers").setPolicyAll(PolicyType.READ_ONLY),
        new MinioBucket("album").setPolicyAll(PolicyType.READ_ONLY),
        new MinioBucket("video").setPolicyAll(PolicyType.READ_ONLY)
    };
    private static final MinioBucket BUCKET_COVER = BUCKETS[0];
    private static final MinioBucket BUCKET_ALBUM = BUCKETS[1];
    private static final MinioBucket BUCKET_VIDEO = BUCKETS[2];
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
     * Uploads the preview to the server with the given arguments as the path in the bucket.
     */
    public String uploadPreview(@Nonnull URL previewUrl, @Nonnull Source source)
        throws NotFoundException {
        return this.uploadURL(previewUrl, Filetype.VIDEO, BUCKET_VIDEO, source, "preview", -1);
    }

    /**
     * Uploads the video to the server with the given arguments as the path in the bucket.
     */
    public String uploadVideo(@Nonnull URL videoUrl, @Nonnull Source source)
        throws NotFoundException {
        // todo upload video
        return videoUrl.toString();
    }

    /**
     * Uploads the cover to the server with the given source as the path in the bucket.
     */
    public String uploadCover(@Nonnull CoverSupplier supplier, @Nonnull Source source)
        throws NotFoundException {
        return this.uploadURL(supplier.getCoverURL(), Filetype.IMAGE, BUCKET_COVER, source, "", -1);
    }

    /**
     * Uploads an album of images to the server with the given arguments as the path in the bucket.
     */
    public List<String> uploadAlbum(@Nonnull List<URL> images, @Nonnull Source source)
        throws NotFoundException {
        List<String> result = new ArrayList<>(images.size());
        for (int i = 0, albumSize = images.size(); i < albumSize; i++) {
            URL url = images.get(i);
            if (null == url) {
                continue;
            }
            result.add(this.uploadURL(url, Filetype.IMAGE, BUCKET_ALBUM, source, "", i));
        }
        return result;
    }

    /**
     * Uploads a file to the server.
     *
     * @param url      the url of the file to be uploaded
     * @param filetype type of the file
     * @param bucket   the bucket to which the file is uploaded
     * @param source   the source of the file
     * @param prefix   the prefix of the path where the file is uploaded, may null
     * @param index    the index of the file under the specified source, may -1
     * @return the path where the file is stored in the server, or the source url if failed
     * @throws NotFoundException if the file is not found
     */
    public String uploadURL(URL url, @Nonnull Filetype filetype, MinioBucket bucket,
        @Nonnull Source source, String prefix, int index) throws NotFoundException {
        Objects.requireNonNull(url, "the url to upload must not be null");
        Objects.requireNonNull(source.getSname());
        if (!filetype.check(url.getFile())) {
            return url.toString();
        }
        File file = null;
        try {
            file = this.download(url);
        } catch (OtherResponseException e) {
            return url.toString();
        }
        String folder = source.getSname() + Constants.URL_PATH_SEPARATOR + source.getSubtype();
        if (StringUtils.isNotBlank(prefix)) {
            folder = prefix + Constants.URL_PATH_SEPARATOR + folder;
        }
        String basename;
        if (index < 0) {
            basename = String.valueOf(source.getRid());
        } else {
            folder += Constants.URL_PATH_SEPARATOR + source.getRid();
            basename = String.valueOf(index);
        }
        return this.uploadLocal(file, bucket, folder, basename);
    }

    /**
     * Uploads a local file to the given folder under the bucket with a specific filename.
     *
     * @param file     the file to upload, must not be null
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
        String target = FileUtilExt.copyExtension(file.getName(), basename);
        if (folder != null) {
            target = folder + Constants.URL_PATH_SEPARATOR + target;
        }
        try {
            this.client().putObject(bucket.getName(), target, file.getPath());
            log.trace("Uploaded {} to {}/{}", file, bucket.getName(), target);
            return this.client().getObjectUrl(bucket.getName(), target);
        } catch (InvalidBucketNameException | XmlPullParserException | NoSuchAlgorithmException
            | InsufficientDataException | IOException | InvalidKeyException | NoResponseException
            | ErrorResponseException | InternalException | InvalidArgumentException e) {
            throw new AppException(e);
        }
    }

    private File download(@Nonnull URL url) throws NotFoundException, OtherResponseException {
        try {
            String file = StringUtils.stripEnd(url.getFile(), Constants.URL_PATH_SEPARATOR);
            String path = FilenameUtils.getPath(StringUtilsExt.toFilename(file));
            File dir = tmpdir;
            if (path != null) {
                dir = new File(dir, path);
            }
            return manager.downloader().download(dir, url);
        } catch (ConnectException e) {
            if (CONNECTION_REFUSED.equals(e.getMessage())) {
                throw new OtherResponseException(HttpStatus.SC_FORBIDDEN, CONNECTION_REFUSED);
            }
            throw new AppException(e);
        } catch (UnknownHostException e) {
            throw new OtherResponseException(HttpStatus.SC_BAD_REQUEST, e.getMessage());
        } catch (FileNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (SocketTimeoutException e) {
            throw new OtherResponseException(HttpStatus.SC_REQUEST_TIMEOUT, e.getMessage());
        } catch (SSLException | SocketException e) {
            throw new OtherResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (HttpResponseException e) {
            throw SiteUtils.handleException(e);
        } catch (IOException e) {
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
        throws InvalidBucketNameException, NoSuchAlgorithmException, InsufficientDataException,
        IOException, InvalidKeyException, NoResponseException, XmlPullParserException,
        ErrorResponseException, InternalException, io.minio.errors.RegionConflictException,
        io.minio.errors.InvalidObjectPrefixException {
        MinioClient client = this.client();
        try {
            for (MinioBucket bucket : BUCKETS) {
                String name = bucket.getName();
                if (!client.bucketExists(name)) {
                    client.makeBucket(name);
                }
                for (Map.Entry<String, PolicyType> entry : bucket.getPolicies().entrySet()) {
                    client.setBucketPolicy(name, entry.getKey(), entry.getValue());
                }
            }
        } catch (ConnectException e) {
            log.error(e.getMessage());
        }
    }
}
