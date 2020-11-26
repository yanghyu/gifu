# 分布式ID生成策略（gifu-data-leaf）

## 项目介绍

本项目是针对美团Leaf项目的Segment模式进行的改进开发。由于某些场景需要，本人在实际项目开发中使用了Segment模式进行ID生成。阅读代码后收获颇多,其中的一些思想帮我解决了棘手的问题。

> 美团Leaf项目是美团订单ID生成的一个需求。具体设计文档见：[ leaf 美团分布式ID生成服务 ](https://tech.meituan.com/MT_Leaf.html )。目前Leaf覆盖了美团点评公司内部金融、餐饮、外卖、酒店旅游、猫眼电影等众多业务线。
> > There are no two identical leaves in the world.
> >
> > 世界上没有两片完全相同的树叶。
> >
> ​							— 莱布尼茨

Leaf项目的Segment模式核心逻辑思想是很优秀的，在保证基本有序性的情况下，还能具有高性能与高扩展性。不过代码逻辑有点啰嗦，代码逻辑不够清晰，阅读起来比较费力。自己阅读后自己感觉还有很多可以优化空间，因此抽出自己闲暇时间开发了此项目。

本项目在开发过程中主要将代码逻辑进行认真梳理，各个环节进行逻辑解耦，使用JDK中成熟的数据结构替换掉原代码中的数据结构，力求代码逻辑清晰易懂，降低隐藏BUG的可能性。

## 主要变化

1.从数据库获取号码段Segment使用乐观锁方式进行并发控制。

2.使用LinkedBlockingQueue队列进行Segment的预加载。

3.充分利用懒加载，减少定时器的使用。

4.自适应步长增加最大和最小范围限制。

5.不存在的key将会自动创建。

## 项目情况
### 源代码位置
`https://github.com/yanghyu/gifu/tree/master/gifu-data/gifu-data-leaf`

### 核心代码
详见实现类:
`com.github.yanghyu.gifu.data.leaf.segment.generater.IdGeneratorImpl`
代码如下:
```
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
     * 最小步长
     */
    private static final int MIN_STEP = 1000;

    /**
     * 最大步长
     */
    private static final int MAX_STEP = 500000;

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
                        applyLoadNewSegment(segmentQueue, key, MIN_STEP);
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
        if (step < MIN_STEP) {
            step = MIN_STEP;
        } else if (step > MAX_STEP) {
            step = MAX_STEP;
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

```

### 如何使用
代码示例:`https://github.com/yanghyu/gifu/tree/master/gifu-data/gifu-data-service/gifu-data-service-leaf`

依赖引用:
```

    <dependencyManagement>
        <dependencies>
            <dependency>
                <artifactId>gifu-data-starter</artifactId>
                <groupId>com.github.yanghyu</groupId>
                <version>1.1.0-RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <dependencies>
        <dependency>
            <groupId>com.github.yanghyu</groupId>
            <artifactId>gifu-data-starter-leaf</artifactId>
        </dependency>
    </dependencies>
    
```






 

