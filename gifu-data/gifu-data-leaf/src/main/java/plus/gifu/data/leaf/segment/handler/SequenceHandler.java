package plus.gifu.data.leaf.segment.handler;

import plus.gifu.data.leaf.segment.dao.KeySequenceDao;
import plus.gifu.data.leaf.segment.exception.NoSuchKeyException;
import plus.gifu.data.leaf.segment.model.KeySequence;
import plus.gifu.data.leaf.segment.model.SequenceSegment;

import javax.sql.DataSource;

/**
 * 序列处理器
 *
 * @author yanghongyu
 * @since 2020-08-25
 */
public class SequenceHandler {

    private final KeySequenceDao keySequenceDao;

    public SequenceHandler(DataSource dataSource) {
        this.keySequenceDao = new KeySequenceDao(dataSource);
    }

    public SequenceSegment getSegment(String key, Integer step) {
        Integer updateCount;
        KeySequence keySequence;
        do {
            keySequence = keySequenceDao.get(key);
            if (keySequence == null) {
                throw new NoSuchKeyException();
            }
            keySequence.setMaxId(keySequence.getMaxId() + step);
            keySequence.setUpdateTimestamp(System.currentTimeMillis());
            keySequence.setVersion(keySequence.getVersion() + 1);
            updateCount = keySequenceDao.updateMaxId(keySequence);
        } while (updateCount < 1);
        SequenceSegment sequenceSegment = new SequenceSegment();
        sequenceSegment.setMaxId(keySequence.getMaxId());
        sequenceSegment.setSequenceId(keySequence.getMaxId() - step);
        sequenceSegment.setStep(step);
        return sequenceSegment;
    }

}
