package plus.gifu.data.leaf.segment.generater;

import plus.gifu.data.leaf.segment.handler.SegmentHandler;
import plus.gifu.data.leaf.segment.model.IdResult;
import plus.gifu.data.leaf.segment.model.Segment;

/**
 * ID 生成器
 *
 * @author yanghongyu
 * @since 2020-08-06
 */
public class IdGeneratorImpl implements IdGenerator {

    private final SegmentHandler segmentHandler;

    public IdGeneratorImpl(SegmentHandler segmentHandler) {
        this.segmentHandler = segmentHandler;
    }

    @Override
    public IdResult generateId(String key) {
        Segment segment = null;
        IdResult idResult = new IdResult();
        segment = segmentHandler.getSegment(key, 5);
        idResult.setId(segment.getMaxId());
        return idResult;
    }

}
