package wsg.tools.common.converter.base;

/**
 * Base converter to implement {@link Converter}.
 *
 * @author Kingen
 * @since 2020/7/21
 */
public abstract class BaseConverter<S, T> implements Converter<S, T> {

    private final Class<S> sClass;
    private final Class<T> tClass;

    public BaseConverter(Class<S> sClass, Class<T> tClass) {
        this.sClass = sClass;
        this.tClass = tClass;
    }

    @SuppressWarnings("unchecked")
    public BaseConverter(Class<?> sClass, Class<T> tClass, boolean dummy) {
        this.sClass = (Class<S>) sClass;
        this.tClass = tClass;
    }

    public Class<S> getSourceType() {
        return sClass;
    }

    public Class<T> getTargetType() {
        return tClass;
    }
}
