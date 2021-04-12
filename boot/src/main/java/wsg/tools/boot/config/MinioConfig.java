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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.io.FileUtilExt;
import wsg.tools.common.io.Filetype;
import wsg.tools.common.io.NotFiletypeException;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.info.adult.view.AlbumSupplier;
import wsg.tools.internet.info.adult.view.PreviewSupplier;
import wsg.tools.internet.info.adult.view.VideoSupplier;

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
    public String uploadPreview(@Nonnull PreviewSupplier supplier, @Nonnull Source source)
        throws NotFiletypeException, NotFoundException, OtherResponseException {
        String folder = "preview";
        folder += Constants.URL_PATH_SEPARATOR + source.getDomain();
        folder += Constants.URL_PATH_SEPARATOR + source.getSubtype();
        String rid = String.valueOf(source.getRid());
        return uploadURL(supplier.getPreviewURL(), Filetype.VIDEO, BUCKET_VIDEO, folder, rid);
    }

    /**
     * Uploads the video to the server with the given arguments as the path in the bucket.
     */
    public String uploadVideo(@Nonnull VideoSupplier supplier, @Nonnull Source source)
        throws NotFiletypeException, NotFoundException, OtherResponseException {
        // todo upload video
        return supplier.getVideoURL().toString();
    }

    /**
     * Uploads the cover to the server with the given source as the path in the bucket.
     *
     * @see #uploadCover(CoverSupplier, String, int, String)
     */
    public String uploadCover(CoverSupplier supplier, @Nonnull Source source)
        throws NotFoundException, OtherResponseException, NotFiletypeException {
        String id = String.valueOf(source.getRid());
        return uploadCover(supplier, source.getDomain(), source.getSubtype(), id);
    }

    /**
     * Uploads the cover to the server with the given arguments as the path in the bucket.
     */
    public String uploadCover(@Nonnull CoverSupplier supplier, String domain, int subtype,
        String id) throws NotFiletypeException, NotFoundException, OtherResponseException {
        String folder = domain + Constants.URL_PATH_SEPARATOR + subtype;
        return uploadURL(supplier.getCoverURL(), Filetype.IMAGE, BUCKET_COVER, folder, id);
    }

    /**
     * Uploads an album of images to the server with the given arguments as the path in the bucket.
     */
    public List<String> uploadAlbum(@Nonnull AlbumSupplier supplier, String domain, int subtype,
        String id) throws NotFoundException, OtherResponseException, NotFiletypeException {
        List<URL> album = supplier.getAlbum();
        List<String> result = new ArrayList<>(album.size());
        String folder = String.join("/", domain, String.valueOf(subtype), id);
        for (int i = 0, albumSize = album.size(); i < albumSize; i++) {
            String sid = String.valueOf(i);
            result.add(uploadURL(album.get(i), Filetype.IMAGE, BUCKET_ALBUM, folder, sid));
        }
        return result;
    }

    private String uploadURL(URL url, Filetype filetype, MinioBucket bucket, String folder,
        String basename) throws NotFiletypeException, NotFoundException, OtherResponseException {
        if (url == null) {
            return null;
        }
        if (!filetype.test(url.getFile())) {
            throw new NotFiletypeException(filetype, url.getFile());
        }
        File file = download(url);
        return uploadLocal(file, bucket, folder, basename);
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
            client().putObject(bucket.getName(), target, file.getPath());
            log.info("Uploaded {} to {}/{}", file, bucket.getName(), target);
            return client().getObjectUrl(bucket.getName(), target);
        } catch (InvalidBucketNameException | XmlPullParserException | NoSuchAlgorithmException | InsufficientDataException | IOException | InvalidKeyException | NoResponseException | ErrorResponseException | InternalException | InvalidArgumentException e) {
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
            String message = e.getMessage();
            throw new OtherResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR, message);
        } catch (OtherResponseException e) {
            throw e;
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
        throws InvalidBucketNameException, NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, NoResponseException, XmlPullParserException, ErrorResponseException, InternalException, io.minio.errors.RegionConflictException, io.minio.errors.InvalidObjectPrefixException {
        MinioClient client = client();
        for (MinioBucket bucket : BUCKETS) {
            String name = bucket.getName();
            if (!client.bucketExists(name)) {
                client.makeBucket(name);
            }
            for (Map.Entry<String, PolicyType> entry : bucket.getPolicies().entrySet()) {
                client.setBucketPolicy(name, entry.getKey(), entry.getValue());
            }
        }
    }
}
