package wsg.tools.internet.info.adult.entry;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import wsg.tools.common.lang.AssertUtils;

/**
 * An adult entry with simple information.
 *
 * @author Kingen
 * @since 2021/3/28
 */
public class BasicAdultEntry {

    private final String code;
    private String title;
    private String description;
    private List<URL> images;

    BasicAdultEntry(String code) {
        this.code = AssertUtils.requireNotBlank(code, "code of an adult entry");
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public List<URL> getImages() {
        return images;
    }

    void setImages(List<URL> images) {
        if (CollectionUtils.isNotEmpty(images)) {
            this.images = Collections.unmodifiableList(images);
        }
    }
}
