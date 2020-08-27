package plus.gifu.data.leaf.segment.exception;

/**
 * 陷入死循环异常
 *
 * @author yanghongyu
 */
public class TooManyLoopException extends LeafException {
    public TooManyLoopException() {
        super("-2", "陷入死循环异常");
    }
}
