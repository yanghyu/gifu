package com.github.yanghyu.gifu.data.leaf.segment.generater;

import com.github.yanghyu.gifu.data.leaf.segment.exception.LeafException;
import com.github.yanghyu.gifu.data.leaf.segment.handler.SegmentHandler;
import com.github.yanghyu.gifu.data.leaf.segment.message.Message;
import com.github.yanghyu.gifu.data.leaf.segment.model.IdResult;
import com.github.yanghyu.gifu.data.leaf.segment.model.SegmentQueue;
import com.github.yanghyu.gifu.data.leaf.segment.util.LoopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.yanghyu.gifu.data.leaf.segment.model.Segment;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

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
    private static final long LOAD_SEGMENT_MAX_TIMEOUT = 30000L;

    /**
     * 一个分段期望维持时间为15分钟
     */
    private static final long EXPECTED_SEGMENT_DURATION = 900000L;

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
        IdResult idResult = new IdResult(Message.SUCCESS);
        if (key == null || "".equals(key)) {
            idResult.setMessage(Message.EMPTY_PARAMETER_EXCEPTION);
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

            try {
                // 2.尝试获取ID
                int roll = 0;
                while (true) {
                    Segment segment = segmentQueue.peek();
                    if (segment != null) {
                        long sequenceId = segment.getSequenceId().getAndIncrement();
                        long remain = segment.getMaxId() - sequenceId;
                        if (remain > 0) {
                            idResult.setId(sequenceId);
                            // 判断是否需要触发线程开启加载新的分段
                            if (remain * 2 < segment.getStep() && segmentQueue.size() < 2 && isSegmentNotLoading(segmentQueue)) {
                                int step = calculateStep(segment.getStep(), segment.getUpdateTimestamp());
                                applyLoadNewSegment(segmentQueue, key, step);
                            }
                            break;
                        } else {
                            segmentQueue.remove(segment);
                        }
                    } else if (segmentQueue.size() < 2 && isSegmentNotLoading(segmentQueue)){
                        applyLoadNewSegment(segmentQueue, key, DEFAULT_STEP);
                    }
                    roll = LoopUtil.loopStatus(roll);
                }
            } catch (LeafException le) {
                idResult.setResultCode(le.getResultCode());
                idResult.setResultMessage(le.getResultMessage());
                logger.error("generate id leaf exception, key:{}", key);
                logger.error("generate id leaf exception", le);
            } catch (RuntimeException e) {
                idResult = new IdResult(Message.SYSTEM_EXCEPTION);
                logger.error("generate id exception, key:{}", key);
                logger.error("generate id exception", e);
            }
        }
        return idResult;
    }

    /**
     * 计算步长
     * @param currentStep 当前步长
     * @param lastLoadTimestamp 上次加载时间戳
     * @return 计算出的步长
     */
    private int calculateStep(int currentStep, long lastLoadTimestamp) {
        int step = currentStep;
        long intervalMillis = System.currentTimeMillis() - lastLoadTimestamp;
        intervalMillis = Math.max(intervalMillis, EXPECTED_SEGMENT_DURATION / 100);
        if (intervalMillis < EXPECTED_SEGMENT_DURATION / 2) {
            step = (int) (currentStep * (Math.round((double) (EXPECTED_SEGMENT_DURATION / intervalMillis)) + 1));
        } else if (intervalMillis < EXPECTED_SEGMENT_DURATION) {
            step = currentStep * 2;
        } else if (intervalMillis > EXPECTED_SEGMENT_DURATION * 2) {
            step = currentStep / 2;
        }
        logger.info("calculate step size, current step:{} interval millis:{} new step:{}", currentStep, intervalMillis, step);
        return step;
    }

    /**
     * 判断Segment加载状态
     *
     * @param segmentQueue 分段对列
     * @return 是否不在加载中
     */
    private boolean isSegmentNotLoading(SegmentQueue segmentQueue) {
        Lock loadReadLock = segmentQueue.getLoadReadLock();
        loadReadLock.lock();
        boolean flag = false;
        try {
            if (segmentQueue.isLoading()) {
                flag = System.currentTimeMillis() - segmentQueue.getLoadStartTimeMillis() <= LOAD_SEGMENT_MAX_TIMEOUT;
            }
        } finally {
            loadReadLock.unlock();
        }
        return !flag;
    }

    /**
     * 申请加载新的Segment
     *
     * @param segmentQueue 分段队列
     * @param key 键
     * @param step 步长
     */
    private void applyLoadNewSegment(SegmentQueue segmentQueue, String key, int step) {
        Lock loadWriteLock = segmentQueue.getLoadWriteLock();
        loadWriteLock.lock();
        try {
            if (segmentQueue.isLoading()) {
                if (System.currentTimeMillis() - segmentQueue.getLoadStartTimeMillis() > LOAD_SEGMENT_MAX_TIMEOUT) {
                    segmentQueue.setLoading(false);
                }
            }
            if (!segmentQueue.isLoading()) {
                segmentQueue.setLoading(true);
                segmentQueue.setLoadStartTimeMillis(System.currentTimeMillis());
                logger.info("apply load segment, key:{} step:{}", key, step);
                executorService.execute(() -> {
                    try {
                        // 这里在逻辑上不一定能够成功加载回一个分段
                        Segment segment = segmentHandler.getSegment(key, step);
                        if (segment != null) {
                            logger.info("load segment success, {}", segment);
                            segmentQueue.offer(segment);
                        }
                    } catch (RuntimeException e) {
                        logger.error("load segment exception, key:{} step:{}", key, step);
                        logger.error("load segment exception", e);
                    } finally {
                        Lock taskLoadWriteLock = segmentQueue.getLoadWriteLock();
                        taskLoadWriteLock.lock();
                        segmentQueue.setLoading(false);
                        taskLoadWriteLock.unlock();
                    }
                });
            }
        } catch (RuntimeException e) {
            logger.error("apply load segment exception, key:{} step:{}", key, step);
            logger.error("apply load segment exception", e);
            throw e;
        } finally {
            loadWriteLock.unlock();
        }
    }

}
