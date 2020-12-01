package wsg.tools.common.io.multimedia;

import java.util.Map;

/**
 * Info of metadata.
 *
 * @author Kingen
 * @since 2020/12/2
 */
public class MetadataInfo {

    private Map<String, String> metadata;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
