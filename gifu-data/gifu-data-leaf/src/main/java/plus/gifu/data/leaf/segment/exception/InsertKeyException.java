package plus.gifu.data.leaf.segment.exception;

import plus.gifu.data.leaf.segment.message.LeafMessage;

/**
 * 新增Key异常
 *
 * @author yanghongyu
 */
public class InsertKeyException extends LeafException {

    public InsertKeyException() {
        super(LeafMessage.INSERT_KEY_EXCEPTION);
    }

}
