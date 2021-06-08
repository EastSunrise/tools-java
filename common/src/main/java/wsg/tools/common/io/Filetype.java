package wsg.tools.common.io;

import java.io.File;
import java.util.Collection;
import javax.annotation.Nonnull;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.Constants;

/**
 * Common types of files. The type of a file is decided by its extension, ignoring case.
 *
 * @author Kingen
 * @since 2021/3/27
 */
public enum Filetype {
    /**
     * Video files
     */
    VIDEO("3gp", "avi", "f4v", "flv", "mkv", "mov", "mp4", "mpeg", "rmvb", "ts", "wmv"),
    /**
     * Audio files
     */
    AUDIO("aac", "flac", "m4a", "mp3"),
    /**
     * Subtitle files
     */
    SUBTITLE("ass", "srt"),
    /**
     * Images
     */
    IMAGE("bmp", "gif", "jpeg", "jpg", "png", "webp"),
    /**
     * Thunder-related files
     */
    THUNDER("xltd");

    private final String[] extensions;

    Filetype(String... extensions) {
        this.extensions = extensions;
    }

    @Nonnull
    public String[] getSuffixes() {
        String[] suffixes = new String[this.getExtensions().length];
        for (int i = 0; i < extensions.length; i++) {
            suffixes[i] = Constants.EXTENSION_SEPARATOR + extensions[i];
        }
        return suffixes;
    }

    public String[] getExtensions() {
        return extensions.clone();
    }

    /**
     * Returns {@code true} if the given name is the current type.
     *
     * @param filename name of the file to be test, not null
     */
    public boolean check(@Nonnull String filename) {
        String extension = FilenameUtils.getExtension(filename);
        return StringUtils.equalsAnyIgnoreCase(extension, extensions);
    }

    /**
     * Returns {@code true} if the given file is of current type.
     *
     * @param file the file to be test, not null
     */
    public boolean check(@Nonnull File file) {
        return this.check(file.getName());
    }

    /**
     * Constructs a suffix filter for files of current type.
     */
    @Nonnull
    public SuffixFileFilter filter() {
        return new SuffixFileFilter(this.getSuffixes(), IOCase.INSENSITIVE);
    }

    /**
     * Finds files of current type within a given directory (and optionally its subdirectories).
     *
     * @see FileUtils#listFiles(File, IOFileFilter, IOFileFilter)
     */
    @Nonnull
    public Collection<File> listFiles(File directory, boolean recursive) {
        IOFileFilter filter = recursive ? TrueFileFilter.TRUE : FalseFileFilter.FALSE;
        return FileUtils.listFiles(directory, this.filter(), filter);
    }
}
