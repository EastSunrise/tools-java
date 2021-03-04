package wsg.tools.internet.download;

/**
 * Strategies when a file to download exists.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public enum FileExistStrategy {
    /**
     * Replace the file that exists.
     */
    REPLACE,
    /**
     * Not execute the task and return
     */
    FINISH,
    /**
     * Append a number after the filename
     */
    RENAME
}
