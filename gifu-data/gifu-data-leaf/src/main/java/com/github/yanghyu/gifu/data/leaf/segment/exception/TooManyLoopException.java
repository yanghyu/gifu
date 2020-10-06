package com.github.yanghyu.gifu.data.leaf.segment.exception;

import com.github.yanghyu.gifu.data.leaf.segment.message.Message;

/**
 * 陷入死循环异常
 *
 * @author yanghongyu
 */
public class TooManyLoopException extends LeafException {

    public TooManyLoopException() {
        super(Message.TOO_MANY_LOOP_EXCEPTION);
    }

}
