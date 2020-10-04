package plus.gifu.data.leaf.segment.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.gifu.data.leaf.segment.dao.SequenceDao;
import plus.gifu.data.leaf.segment.exception.InsertKeyException;
import plus.gifu.data.leaf.segment.model.Segment;
import plus.gifu.data.leaf.segment.model.Sequence;
import plus.gifu.data.leaf.segment.util.LoopUtil;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 序列段处理器
 *
 * @author yanghongyu
 * @since 2020-08-25
 */
public class SegmentHandler {

    private static final Logger logger = LoggerFactory.getLogger(SegmentHandler.class);

    private final SequenceDao sequenceDao;

    public SegmentHandler(DataSource dataSource) {
        this.sequenceDao = new SequenceDao(dataSource);
    }

    public Segment getSegment(String key, int step) {
        int updateCount, roll = 0;
        Sequence sequence;
        do {
            roll = LoopUtil.loopStatus(roll);
            sequence = sequenceDao.get(key);
            if (sequence == null) {
                synchronized (this) {
                    sequence = sequenceDao.get(key);
                    if (sequence == null) {
                        sequence = insertKey(key, step);
                    }
                }
                if (sequence == null) {
                    throw new InsertKeyException();
                }
            }
            sequence.setMaxId(sequence.getMaxId() + step);
            sequence.setUpdateTimestamp(System.currentTimeMillis());
            sequence.setVersion(sequence.getVersion() + 1);
            updateCount = sequenceDao.updateMaxId(sequence);
        } while (updateCount < 1);
        Segment segment = new Segment();
        segment.setKey(key);
        segment.setMaxId(sequence.getMaxId());
        segment.setSequenceId(new AtomicLong(sequence.getMaxId() - step));
        segment.setStep(step);
        segment.setUpdateTimestamp(sequence.getUpdateTimestamp());
        return segment;
    }

    public Sequence insertKey(String key, int step) {
        Sequence sequence = new Sequence();
        sequence.setKey(key);
        sequence.setMaxId(1L + step);
        sequence.setVersion(0L);
        long timestamp = System.currentTimeMillis();
        sequence.setCreateTimestamp(timestamp);
        sequence.setUpdateTimestamp(timestamp);
        sequence.setDeleteFlag(false);
        int count = 0;
        try {
            count = sequenceDao.insert(sequence);
        } catch (RuntimeException e) {
            logger.error("insert sequence key exception, key:{} step:{}", key, step);
            logger.error("insert sequence key exception", e);
        }
        if (count > 0) {
            return sequence;
        } else {
            return null;
        }
    }

}
