package wsg.tools.internet.info.adult.entry;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Builds an {@link AbstractAdultEntry}.
 *
 * @author Kingen
 * @since 2021/3/3
 */
public class BasicAdultEntryBuilder {

    private final String code;
    private String title;
    private String description;
    private List<URL> images;

    BasicAdultEntryBuilder(String code) {
        this.code = code;
    }

    /**
     * Initializes a builder of type {@link BasicAdultEntry} by the given code.
     */
    @Nonnull
    public static BasicAdultEntryBuilder builder(@Nonnull String code) {
        return new BasicAdultEntryBuilder(code);
    }

    /**
     * Builds an instance of {@link BasicAdultEntry}.
     */
    public BasicAdultEntry basic() {
        return constructBasic(BasicAdultEntry::new);
    }

    /**
     * Builds an instance of {@link AmateurAdultEntry} without a performer.
     */
    public AmateurAdultEntry amateur() {
        return constructBasic(co -> new AmateurAdultEntry(co, null));
    }

    /**
     * Builds an instance of {@link FormalAdultEntry} without any actresses.
     */
    public FormalAdultEntry formal() {
        return constructBasic(co -> new FormalAdultEntry(co, null));
    }

    /**
     * Verifies the consistency between current code and the given code, ignoring if the given code
     * is null.
     *
     * @throws IllegalArgumentException if the two codes are conflict,
     */
    public BasicAdultEntryBuilder validateCode(String code) {
        if (code == null || code.equalsIgnoreCase(this.code)) {
            return this;
        }
        String message = String.format("The two code are conflict: '%s' and '%s'", code, this.code);
        throw new IllegalArgumentException(message);
    }

    public BasicAdultEntryBuilder title(@Nonnull String title) {
        this.title = title;
        return this;
    }

    public BasicAdultEntryBuilder description(@Nonnull String description) {
        this.description = description;
        return this;
    }

    public BasicAdultEntryBuilder images(List<URL> images) {
        if (CollectionUtils.isNotEmpty(images)) {
            this.images = Collections.unmodifiableList(images);
        }
        return this;
    }

    <T extends BasicAdultEntry> T constructBasic(@Nonnull Function<String, T> constructor) {
        T entry = constructor.apply(code);
        entry.setTitle(title);
        entry.setDescription(description);
        entry.setImages(images);
        return entry;
    }
}
