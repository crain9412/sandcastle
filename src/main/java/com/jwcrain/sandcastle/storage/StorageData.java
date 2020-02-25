package com.jwcrain.sandcastle.storage;

public class StorageData {
    private long offset;
    private int size;

    public StorageData(long offset, int size) {
        this.offset = offset;
        this.size = size;
    }

    public long getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }
}
