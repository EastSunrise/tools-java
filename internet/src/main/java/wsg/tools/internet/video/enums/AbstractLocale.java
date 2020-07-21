package wsg.tools.internet.video.enums;

import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.jackson.intf.AkaPredicate;
import wsg.tools.common.jackson.intf.CodeSupplier;
import wsg.tools.common.jackson.intf.TextSupplier;
import wsg.tools.common.jackson.intf.TitleSupplier;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Extension of @{@link Locale}
 *
 * @author Kingen
 * @since 2020/6/23
 */
public abstract class AbstractLocale<T extends AbstractLocale<T>> implements CodeSupplier<String>, TextSupplier, TitleSupplier, AkaPredicate<String>, Serializable {

    protected final String code;
    protected final String text;
    protected final String title;
    protected final String[] aka;

    protected AbstractLocale(String code, String text, String title, String[] aka) {
        this.code = code;
        this.text = text;
        this.title = title;
        this.aka = aka;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public boolean alsoKnownAs(String other) {
        if (aka == null) {
            return false;
        }
        return ArrayUtils.contains(aka, other);
    }

    @Override
    public String toString() {
        return text;
    }
}
