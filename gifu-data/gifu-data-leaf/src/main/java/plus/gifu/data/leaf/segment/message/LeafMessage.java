package plus.gifu.data.leaf.segment.message;

public enum LeafMessage {

    INSERT_KEY_EXCEPTION("-1", "新增Key异常"),

    TOO_MANY_LOOP_EXCEPTION("-2", "陷入死循环异常"),

    ;

    /**
     * 结果码
     */
    private final String resultCode;

    /**
     * 结果信息
     */
    private final String resultMessage;

    LeafMessage(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

}
