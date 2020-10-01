package plus.gifu.data.leaf.segment.generater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.gifu.data.leaf.segment.handler.SegmentHandler;
import plus.gifu.data.leaf.segment.model.IdResult;
import plus.gifu.data.leaf.segment.model.Segment;
import plus.gifu.data.leaf.segment.model.SegmentQueue;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

import static plus.gifu.data.leaf.segment.message.Message.EMPTY_PARAMETER_EXCEPTION;
import static plus.gifu.data.leaf.segment.message.Message.SUCCESS;

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
            // 2.获取分段
            Lock readLock = segmentQueue.getReadLock();
            readLock.lock();
            try {
                Segment segment = null;
                do {
                    segment = segmentQueue.peek();
                    if (segment == null) {

                    }
                } while (segment == null);
            } finally {
                readLock.unlock();
            }
            idResult.setId(1L);
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
                    } catch (Exception e) {
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
        } catch (Exception e) {
            logger.error("apply load segment error, key:{} step:{}", key, step);
            logger.error("apply load segment error", e);
        } finally {
            writeLock.unlock();
        }
    }

}
