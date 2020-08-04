package plus.gifu.data.leaf.segment.generater;

/**
 * ID 生成器
 *
 * @author yanghongyu
 * @since 2020-06-18
 */
public interface IdGenerator {

    /**
     * 生成ID
     *
     * @param key   键名
     * @return      ID
     */
    Long generateId(String key);

}
