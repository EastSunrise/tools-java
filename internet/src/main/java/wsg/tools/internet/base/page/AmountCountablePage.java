package wsg.tools.internet.base.page;

/**
 * An extension of {@link Page} whose total amount of elements is available.
 *
 * @author Kingen
 * @since 2021/5/28
 */
public interface AmountCountablePage<T> extends Page<T>, PageCountable, AmountCountable {

}
