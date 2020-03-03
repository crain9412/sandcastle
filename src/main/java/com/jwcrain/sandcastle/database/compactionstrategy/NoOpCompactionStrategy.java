package com.jwcrain.sandcastle.database.compactionstrategy;

public class NoOpCompactionStrategy implements CompactionStrategy {
    @Override
    public boolean shouldCompact(long insertCount) {
        return false;
    }
}
