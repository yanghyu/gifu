package plus.gifu.data.leaf.segment.generater;

import plus.gifu.data.leaf.segment.handler.SegmentHandler;
import plus.gifu.data.leaf.segment.model.IdResult;
import plus.gifu.data.leaf.segment.model.Segment;
import plus.gifu.data.leaf.segment.model.SegmentQueue;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ID 生成器
 *
 * @author yanghongyu
 * @since 2020-08-06
 */
public class IdGeneratorImpl implements IdGenerator {

    private final SegmentHandler segmentHandler;

    private final ConcurrentHashMap<String, SegmentQueue> keySegmentQueueMap = new ConcurrentHashMap<>();

    public IdGeneratorImpl(SegmentHandler segmentHandler) {
        this.segmentHandler = segmentHandler;
    }

    @Override
    public IdResult generateId(String key) {
        if (key == null || "".equals(key)) {

        }
        SegmentQueue segmentQueue = keySegmentQueueMap.get(key);
        IdResult idResult = new IdResult();
        idResult.setId(1L);
        return idResult;
    }

}
