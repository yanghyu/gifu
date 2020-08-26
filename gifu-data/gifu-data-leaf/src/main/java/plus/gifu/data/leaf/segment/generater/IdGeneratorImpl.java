package plus.gifu.data.leaf.segment.generater;

import plus.gifu.data.leaf.segment.exception.NoSuchKeyException;
import plus.gifu.data.leaf.segment.exception.TooManyLoopException;
import plus.gifu.data.leaf.segment.handler.SequenceHandler;
import plus.gifu.data.leaf.segment.model.IdResult;
import plus.gifu.data.leaf.segment.model.SequenceSegment;

/**
 * ID 生成器
 *
 * @author yanghongyu
 * @since 2020-08-06
 */
public class IdGeneratorImpl implements IdGenerator {

    private final SequenceHandler sequenceHandler;

    public IdGeneratorImpl(SequenceHandler sequenceHandler) {
        this.sequenceHandler = sequenceHandler;
    }

    @Override
    public IdResult generateId(String key) {
        SequenceSegment sequenceSegment = null;
        IdResult idResult = new IdResult();
        try {
            sequenceSegment = sequenceHandler.getSegment(key, 5);
            idResult.setId(sequenceSegment.getMaxId());
        } catch (NoSuchKeyException e) {
            e.printStackTrace();
        } catch (TooManyLoopException e) {
            e.printStackTrace();
        }
        return idResult;
    }

}
