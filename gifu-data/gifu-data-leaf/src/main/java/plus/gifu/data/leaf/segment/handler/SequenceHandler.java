package plus.gifu.data.leaf.segment.handler;

import plus.gifu.data.leaf.segment.dao.KeySequenceDao;
import plus.gifu.data.leaf.segment.exception.TooManyLoopException;
import plus.gifu.data.leaf.segment.exception.NoSuchKeyException;
import plus.gifu.data.leaf.segment.model.KeySequence;
import plus.gifu.data.leaf.segment.model.SequenceSegment;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

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

    public SequenceSegment getSegment(String key, Integer step) throws NoSuchKeyException, TooManyLoopException {
        Integer updateCount;
        KeySequence keySequence;
        int roll = 0;
        do {
            if (roll > 10000) {
                throw new TooManyLoopException();
            } else if (roll > 1000) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            keySequence = keySequenceDao.get(key);
            if (keySequence == null) {
                throw new NoSuchKeyException();
            }
            keySequence.setMaxId(keySequence.getMaxId() + step);
            keySequence.setUpdateTimestamp(System.currentTimeMillis());
            keySequence.setVersion(keySequence.getVersion() + 1);
            updateCount = keySequenceDao.updateMaxId(keySequence);
            roll ++;
        } while (updateCount < 1);
        SequenceSegment sequenceSegment = new SequenceSegment();
        sequenceSegment.setMaxId(keySequence.getMaxId());
        sequenceSegment.setSequenceId(keySequence.getMaxId() - step);
        sequenceSegment.setStep(step);
        return sequenceSegment;
    }

}
