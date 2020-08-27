package plus.gifu.data.leaf.segment.handler;

import plus.gifu.data.leaf.segment.dao.KeySequenceDao;
import plus.gifu.data.leaf.segment.exception.TooManyLoopException;
import plus.gifu.data.leaf.segment.exception.InsertKeyException;
import plus.gifu.data.leaf.segment.model.KeySequence;
import plus.gifu.data.leaf.segment.model.Segment;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 序列段处理器
 *
 * @author yanghongyu
 * @since 2020-08-25
 */
public class SegmentHandler {

    private final KeySequenceDao keySequenceDao;

    public SegmentHandler(DataSource dataSource) {
        this.keySequenceDao = new KeySequenceDao(dataSource);
    }

    public Segment getSegment(String key, Integer step) {
        int updateCount, roll = 0;
        KeySequence keySequence;
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
                throw new InsertKeyException();
            }
            keySequence.setMaxId(keySequence.getMaxId() + step);
            keySequence.setUpdateTimestamp(System.currentTimeMillis());
            keySequence.setVersion(keySequence.getVersion() + 1);
            updateCount = keySequenceDao.updateMaxId(keySequence);
            roll ++;
        } while (updateCount < 1);
        Segment segment = new Segment();
        segment.setKey(key);
        segment.setMaxId(keySequence.getMaxId());
        segment.setSequenceId(new AtomicLong(keySequence.getMaxId() - step));
        segment.setStep(step);
        return segment;
    }

}
