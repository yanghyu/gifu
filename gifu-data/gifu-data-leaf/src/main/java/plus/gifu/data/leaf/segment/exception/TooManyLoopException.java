package plus.gifu.data.leaf.segment.exception;

import plus.gifu.data.leaf.segment.message.LeafMessage;

/**
 * 陷入死循环异常
 *
 * @author yanghongyu
 */
public class TooManyLoopException extends LeafException {

    public TooManyLoopException() {
        super(LeafMessage.TOO_MANY_LOOP_EXCEPTION);
    }

}
