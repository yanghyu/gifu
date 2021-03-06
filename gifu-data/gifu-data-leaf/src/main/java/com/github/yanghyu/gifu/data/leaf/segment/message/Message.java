package com.github.yanghyu.gifu.data.leaf.segment.message;

public enum Message {

    SUCCESS("0", "成功"),

    SYSTEM_EXCEPTION("-1", "系统异常"),

    EMPTY_PARAMETER_EXCEPTION("-3", "参数不能为空"),

    INSERT_KEY_EXCEPTION("-8", "新增Key异常"),

    TOO_MANY_LOOP_EXCEPTION("-9", "陷入死循环异常"),

    ;

    /**
     * 结果码
     */
    private final String resultCode;

    /**
     * 结果信息
     */
    private final String resultMessage;

    Message(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    @Override
    public String toString() {
        return "Message{" +
                "resultCode='" + resultCode + '\'' +
                ", resultMessage='" + resultMessage + '\'' +
                '}';
    }

}
