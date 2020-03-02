package com.jwcrain.sandcastle.database.compactionstrategy;

public class SizeBasedCompactionStrategy implements CompactionStrategy {
    private static int MAX_FILE_SIZE = 10000000; // 10M
    private static int EXPECTED_KEY_SIZE = 256;

    @Override
    public boolean shouldCompact(long fileSize, long insertCount) {
        return fileSize > MAX_FILE_SIZE - EXPECTED_KEY_SIZE;
    }
}
