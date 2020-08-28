package plus.gifu.data.leaf.segment.model;

/**
 * Key序列
 *
 * @author yanghongyu
 * @since 2020-06-18
 */
public class Sequence {

    /**
     * 主键
     */
    private String key;

    /**
     * 最大序列号
     */
    private Long maxId;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 创建时间
     */
    private Long createTimestamp;

    /**
     * 修改时间
     */
    private Long updateTimestamp;

    /**
     * 软删除标记
     */
    private Boolean deleteFlag;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
