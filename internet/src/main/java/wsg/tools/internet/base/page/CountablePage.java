package wsg.tools.internet.base.page;

/**
 * An extension of {@link Page} whose number of total pages is available.
 *
 * @author Kingen
 * @since 2021/5/28
 */
public interface CountablePage<T> extends Page<T>, PageCountable {

}
