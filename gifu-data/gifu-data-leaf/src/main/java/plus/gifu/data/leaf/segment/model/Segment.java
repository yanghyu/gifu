package plus.gifu.data.leaf.segment.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 序列分段
 *
 * @author yanghongyu
 * @since 2020-08-21
 */
public class Segment {

    /**
     * 键
     */
    private String key;

    /**
     * 序列号
     */
    private AtomicLong sequenceId = new AtomicLong(0);;

    /**
     * 最大序列号
     */
    private volatile long maxId;

    /**
     * 步长
     */
    private volatile int step;

    /**
     * 修改时间
     */
    private Long updateTimestamp;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AtomicLong getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(AtomicLong sequenceId) {
        this.sequenceId = sequenceId;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    @Override
    public String toString() {
        return "Segment{" +
                "key='" + key + '\'' +
                ", sequenceId=" + sequenceId +
                ", maxId=" + maxId +
                ", step=" + step +
                ", updateTimestamp=" + updateTimestamp +
                '}';
    }
}
