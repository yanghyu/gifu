package plus.data.leaf.segment.model;

/**
 * Key序列
 *
 * @author yanghongyu
 * @since 2020-06-18
 */
public class KeySequence {

    /**
     * 主键
     */
    private String key;

    /**
     * 序列号
     */
    private Long sequenceId;

    /**
     * 步长
     */
    private Integer step;

    /**
     * 版本号
     */
    private Integer version;

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

}
