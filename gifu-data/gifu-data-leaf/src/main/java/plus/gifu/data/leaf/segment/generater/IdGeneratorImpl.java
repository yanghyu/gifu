package plus.gifu.data.leaf.segment.generater;

import plus.gifu.data.leaf.segment.handler.SegmentHandler;
import plus.gifu.data.leaf.segment.model.IdResult;
import plus.gifu.data.leaf.segment.model.SegmentQueue;

import java.util.concurrent.ConcurrentHashMap;

import static plus.gifu.data.leaf.segment.message.Message.EMPTY_PARAMETER_EXCEPTION;
import static plus.gifu.data.leaf.segment.message.Message.SUCCESS;

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
        IdResult idResult = new IdResult(SUCCESS);
        if (key == null || "".equals(key)) {
            idResult.setMessage(EMPTY_PARAMETER_EXCEPTION);
        } else {
            SegmentQueue segmentQueue = keySegmentQueueMap.get(key);
            idResult.setId(1L);
        }
        return idResult;
    }

}
