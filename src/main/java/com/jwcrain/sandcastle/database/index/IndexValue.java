package com.jwcrain.sandcastle.database.index;

import java.util.concurrent.locks.ReentrantLock;

public class IndexValue {
    private long offset;
    private ReentrantLock lock = new ReentrantLock();

    public IndexValue(long offset) {
        this.offset = offset;
    }

    public long getOffset() {
        return offset;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }
}
