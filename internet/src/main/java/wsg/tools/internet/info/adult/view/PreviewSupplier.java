package wsg.tools.internet.info.adult.view;

import java.net.URL;

/**
 * Represents a supplier of the preview of the entry.
 *
 * @author Kingen
 * @since 2021/4/7
 */
public interface PreviewSupplier {

    /**
     * Returns the preview of the entry.
     *
     * @return the preview
     */
    URL getPreview();
}
