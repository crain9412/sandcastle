package com.jwcrain.sandcastle.database.compactionstrategy;

import java.util.Random;

public class RandomizedCompactionStrategy implements CompactionStrategy {
    private static final float CHANCE_OF_COMPACTION = 0.00001f;
    private Random random = new Random(123L); /* To be repeatable for tests, could allow users to pass in seed */

    @Override
    public boolean shouldCompact(long insertCount) {
        return random.nextFloat() < CHANCE_OF_COMPACTION;
    }
}
