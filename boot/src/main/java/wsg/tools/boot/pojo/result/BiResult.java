package wsg.tools.boot.pojo.result;

import lombok.Getter;

/**
 * Result with two records.
 *
 * @author Kingen
 * @since 2020/11/22
 */
@Getter
public class BiResult<L, R> extends BaseResult {

    private final L left;
    private final R right;

    private BiResult(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> BiResult<L, R> of(L left, R right) {
        return new BiResult<>(left, right);
    }

    public static <L, R> BiResult<L, R> empty() {
        return new BiResult<>(null, null);
    }

    public boolean hasLeft() {
        return left != null;
    }

    public boolean hasRight() {
        return right != null;
    }
}
