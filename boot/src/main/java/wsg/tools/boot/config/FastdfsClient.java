package wsg.tools.boot.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.csource.fastdfs.UploadStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Client of FastDFS to upload a file to the server.
 *
 * @author Kingen
 * @since 2021/3/7
 */
@Slf4j
@Component
@PropertySource("classpath:config/fastdfs-client.properties")
public class FastdfsClient implements InitializingBean {

    public static final String META_SOURCE = "source";

    @Value("${fastdfs.tracker_servers}")
    private String trackerServers;
    private StorageClient client;

    /**
     * Uploads a local file to fastdfs.
     *
     * @param file the given file
     * @return the path where the file stores.
     */
    public StorePath uploadLocal(@Nonnull File file, NameValuePair... meta)
        throws IOException, MyException {
        log.info("Uploading a local file to FastDFS: {}", file.getPath());
        String extension = FilenameUtils.getExtension(file.getName());
        String[] paths = getClient().upload_file(file.getPath(), extension, meta);
        return new StorePath(paths[0], paths[1]);
    }

    /**
     * Uploads an uploaded file received in a multipart request.
     *
     * @param file the uploaded file
     * @return the path where the file stores.
     */
    public StorePath uploadMultipart(@Nonnull MultipartFile file, NameValuePair... meta)
        throws IOException, MyException {
        log.info("Uploading a multipart file to FastDFS: {}", file.getName());
        String extension = FilenameUtils.getExtension(file.getName());
        UploadStream stream = new UploadStream(file.getInputStream(), file.getSize());
        String[] paths = getClient().upload_file(null, file.getSize(), stream, extension, meta);
        return new StorePath(paths[0], paths[1]);
    }

    /**
     * Uploads the file identified by the given url.
     *
     * @param urlStr the string of a url pointing to the file
     * @return the path where the file stores.
     */
    public StorePath uploadUrl(@Nonnull String urlStr, NameValuePair... meta)
        throws IOException, MyException {
        log.info("Uploading an online file to FastDFS: {}", urlStr);
        URL url = new URL(urlStr);
        URLConnection connection = url.openConnection();
        String extension = FilenameUtils.getExtension(url.getFile());
        long size = connection.getContentLengthLong();
        UploadStream stream = new UploadStream(connection.getInputStream(), size);
        String[] paths = getClient().upload_file(null, size, stream, extension, meta);
        return new StorePath(paths[0], paths[1]);
    }

    private StorageClient getClient() throws IOException {
        if (client == null || !client.isAvaliable() || !client.isConnected()) {
            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getTrackerServer();
            client = new StorageClient(trackerServer);
        }
        return client;
    }

    @Override
    public void afterPropertiesSet() throws IOException, MyException {
        ClientGlobal.initByTrackers(trackerServers);
    }
}
