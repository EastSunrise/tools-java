package wsg.tools.boot.config;

/**
 * The path where a file stores in the FastDFS.
 *
 * @author Kingen
 * @since 2021/3/7
 */
public class StorePath {

    private static final String SEPARATOR = "/";

    private final String group;
    private final String path;

    public StorePath(String group, String path) {
        this.group = group;
        this.path = path;
    }

    public String getFullPath() {
        return group + SEPARATOR + path;
    }

    public String getGroup() {
        return group;
    }

    public String getPath() {
        return path;
    }
}
