package plus.gifu.data.leaf.segment.model;

import plus.gifu.data.leaf.segment.message.Message;

/**
 * 生成的ID结果
 */
public class IdResult {

    /**
     * 新 ID
     */
    private Long id;

    /**
     * 结果码
     */
    private String resultCode;

    /**
     * 结果信息
     */
    private String resultMessage;

    public IdResult(Message message) {
        this.resultCode = message.getResultCode();
        this.resultMessage = message.getResultMessage();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setMessage(Message message) {
        this.resultCode = message.getResultCode();
        this.resultMessage = message.getResultMessage();
    }
}
