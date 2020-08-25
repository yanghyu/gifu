package plus.gifu.data.leaf.segment.model;

/**
 * 序列分段
 *
 * @author yanghongyu
 * @since 2020-08-21
 */
public class SequenceSegment {

    /**
     * 序列号
     */
    private Long sequenceId;

    /**
     * 最大序列号
     */
    private Long maxId;

    /**
     * 步长
     */
    private Integer step;

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }
}
