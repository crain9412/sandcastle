package com.jwcrain.sandcastle.database.compactionstrategy;

public interface CompactionStrategy {
    boolean shouldCompact(long insertCount);
}
