package plus.gifu.data.leaf.segment.generater;

import plus.gifu.data.leaf.segment.dao.KeySequenceDao;
import plus.gifu.data.leaf.segment.handler.SequenceHandler;
import plus.gifu.data.leaf.segment.model.IdResult;
import plus.gifu.data.leaf.segment.model.KeySequence;
import plus.gifu.data.leaf.segment.model.SequenceSegment;

import javax.sql.DataSource;

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
        SequenceSegment sequenceSegment = sequenceHandler.getSegment(key, 5);
        IdResult idResult = new IdResult();
        idResult.setId(sequenceSegment.getMaxId());
        return idResult;
    }

}
