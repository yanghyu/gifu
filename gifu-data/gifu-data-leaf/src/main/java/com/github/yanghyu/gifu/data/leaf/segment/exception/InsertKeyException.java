package com.github.yanghyu.gifu.data.leaf.segment.exception;

import com.github.yanghyu.gifu.data.leaf.segment.message.Message;

/**
 * 新增Key异常
 *
 * @author yanghongyu
 */
public class InsertKeyException extends LeafException {

    public InsertKeyException() {
        super(Message.INSERT_KEY_EXCEPTION);
    }

}
