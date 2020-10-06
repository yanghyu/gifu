package com.github.yanghyu.gifu.data.leaf.segment.model;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 分段队列
 */
public class SegmentQueue extends LinkedBlockingQueue<Segment> {

    /**
     * 加载中标志
     */
    private boolean loading = false;

    /**
     * 开始加载毫秒值
     */
    private long loadStartTimeMillis = 0;

    /**
     * Lock held by loading, loadStartTimeMillis, etc.
     */
    private final ReentrantReadWriteLock lockReadWriteLock = new ReentrantReadWriteLock();

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public long getLoadStartTimeMillis() {
        return loadStartTimeMillis;
    }

    public void setLoadStartTimeMillis(long loadStartTimeMillis) {
        this.loadStartTimeMillis = loadStartTimeMillis;
    }

    public Lock getLoadReadLock() {
        return lockReadWriteLock.readLock();
    }

    public Lock getLoadWriteLock() {
        return lockReadWriteLock.writeLock();
    }
}
