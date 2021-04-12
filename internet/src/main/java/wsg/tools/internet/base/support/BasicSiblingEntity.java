package wsg.tools.internet.base.support;

import wsg.tools.internet.base.view.SiblingSupplier;

/**
 * A basic implementation of {@link SiblingSupplier}.
 *
 * @author Kingen
 * @since 2021/4/9
 */
public class BasicSiblingEntity<ID> implements SiblingSupplier<ID> {

    private final ID previous;
    private final ID next;

    public BasicSiblingEntity(ID previous, ID next) {
        this.previous = previous;
        this.next = next;
    }

    @Override
    public ID getNextId() {
        return next;
    }

    @Override
    public ID getPreviousId() {
        return previous;
    }
}
