package plus.gifu.data.leaf.segment.exception;

import plus.gifu.data.leaf.segment.message.LeafMessage;

public class LeafException extends RuntimeException {

    /**
     * 结果码
     */
    private String resultCode;

    /**
     * 结果信息
     */
    private String resultMessage;

    public LeafException(LeafMessage message) {
        super();
        this.resultCode = message.getResultCode();
        this.resultMessage = message.getResultMessage();
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

}
