package plus.gifu.data.leaf.segment.generater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.gifu.data.leaf.segment.exception.LeafException;
import plus.gifu.data.leaf.segment.handler.SegmentHandler;
import plus.gifu.data.leaf.segment.model.IdResult;
import plus.gifu.data.leaf.segment.model.Segment;
import plus.gifu.data.leaf.segment.model.SegmentQueue;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

import static plus.gifu.data.leaf.segment.message.Message.*;

/**
 * ID 生成器
 *
 * @author yanghongyu
 * @since 2020-08-06
 */
public class IdGeneratorImpl implements IdGenerator {

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorImpl.class);

    /**
     * 默认步长
     */
    private static final int DEFAULT_STEP = 1000;

    /**
     * 加载新分段最大超时时间（30s）
     */
    private static final long LOAD_MAX_TIMEOUT = 30000;

    /**
     * 分段队列缓存列表
     */
    private final ConcurrentHashMap<String, SegmentQueue> keySegmentQueueMap = new ConcurrentHashMap<>();

    /**
     * 序列段处理器
     */
    private final SegmentHandler segmentHandler;

    /**
     * 异步执行线程池
     */
    private final ExecutorService executorService = new ThreadPoolExecutor(5, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new LoadThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static class LoadThreadFactory implements ThreadFactory {

        private static int num = 0;

        private static synchronized int threadNum() {
            return num++;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-Segment-Load-" + threadNum());
        }

    }

    public IdGeneratorImpl(SegmentHandler segmentHandler) {
        this.segmentHandler = segmentHandler;
    }

    @Override
    public IdResult generateId(String key) {
        IdResult idResult = new IdResult(SUCCESS);
        if (key == null || "".equals(key)) {
            idResult.setMessage(EMPTY_PARAMETER_EXCEPTION);
        } else {
            // 1.获取SegmentQueue对象
            SegmentQueue segmentQueue = keySegmentQueueMap.get(key);
            if (segmentQueue == null) {
                SegmentQueue newSegmentQueue = new SegmentQueue();
                segmentQueue = keySegmentQueueMap.putIfAbsent(key, newSegmentQueue);
                if (segmentQueue == null) {
                    segmentQueue = newSegmentQueue;
                }
            }

            Lock readLock = segmentQueue.getReadLock();
            readLock.lock();
            try {
                // 2.获取ID
                while (true) {
                    Segment segment = segmentQueue.peek();
                    if (segment == null) {
                        applyLoadNewSegment(segmentQueue, key, DEFAULT_STEP);
                        try {
                            TimeUnit.MILLISECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            logger.error("sleep interrupted exception", e);
                        }
                    } else {
                        long sequenceId = segment.getSequenceId().getAndIncrement();
                        if (sequenceId < segment.getMaxId()) {
                            idResult.setId(sequenceId);
                            // TODO 开启线程加载新的分段
                            break;
                        } else {
                            Lock writeLock = segmentQueue.getWriteLock();
                            writeLock.lock();
                            try {
                                segmentQueue.remove(segment);
                            } finally {
                                writeLock.unlock();
                            }
                        }
                    }
                }
            } catch (LeafException le) {
                idResult.setResultCode(le.getResultCode());
                idResult.setResultMessage(le.getResultMessage());
                logger.error("generate id leaf exception, key:{}", key);
                logger.error("generate id leaf exception", le);
            } catch (RuntimeException e) {
                idResult = new IdResult(SYSTEM_EXCEPTION);
                logger.error("generate id exception, key:{}", key);
                logger.error("generate id exception", e);
            } finally {
                readLock.unlock();
            }
        }
        return idResult;
    }

    /**
     * 申请加载新的Segment
     *
     * @param segmentQueue 分段对列
     * @param key 键
     * @param step 步长
     */
    private void applyLoadNewSegment(SegmentQueue segmentQueue, String key, int step) {
        Lock writeLock = segmentQueue.getWriteLock();
        writeLock.lock();
        try {
            if (segmentQueue.isLoading()) {
                if (System.currentTimeMillis() - segmentQueue.getLoadStartTimeMillis() > LOAD_MAX_TIMEOUT) {
                    segmentQueue.setLoading(false);
                }
            }
            if (!segmentQueue.isLoading()) {
                segmentQueue.setLoading(true);
                segmentQueue.setLoadStartTimeMillis(System.currentTimeMillis());
                executorService.execute(() -> {
                    try {
                        Segment segment = segmentHandler.getSegment(key, step);
                        if (segment != null) {
                            segmentQueue.offer(segment);
                        }
                    } catch (RuntimeException e) {
                        logger.error("load segment error, key:{} step:{}", key, step);
                        logger.error("load segment error", e);
                    } finally {
                        Lock loadWriteLock = segmentQueue.getWriteLock();
                        loadWriteLock.lock();
                        segmentQueue.setLoading(false);
                        loadWriteLock.unlock();
                    }
                });
            }
        } catch (RuntimeException e) {
            logger.error("apply load segment error, key:{} step:{}", key, step);
            logger.error("apply load segment error", e);
            throw e;
        } finally {
            writeLock.unlock();
        }
    }

}
