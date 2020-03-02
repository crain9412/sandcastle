package com.jwcrain.sandcastle.database.compactionstrategy;

public class CountingCompactionStrategy implements CompactionStrategy {
    private static final long COMPACT_PER_ENTRY = 100000; /* TODO: this could wrap at large counts */

    @Override
    public boolean shouldCompact(long insertCount) {
        return insertCount % COMPACT_PER_ENTRY == 0 && insertCount != 0;
    }
}
