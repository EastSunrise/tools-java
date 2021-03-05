package wsg.tools.internet.download.base;

/**
 * If the filename of a link is available.
 *
 * @author Kingen
 * @since 2020/12/3
 */
@FunctionalInterface
public interface FilenameSupplier {

    /**
     * Returns the filename of the link.
     *
     * @return the filename
     */
    String getFilename();
}
