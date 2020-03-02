package com.jwcrain.sandcastle.database;

import com.jwcrain.sandcastle.database.compactionstrategy.CompactionStrategy;
import com.jwcrain.sandcastle.database.error.Error;
import com.jwcrain.sandcastle.database.index.Index;
import com.jwcrain.sandcastle.database.storage.Storage;
import org.apache.commons.codec.digest.MurmurHash3;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseImpl implements Database {
    private static final char DELIMITER = '=';
    private static final char END_OF_LINE = '\n';
    private static final char EMPTY_VALUE = ' ';
    private Index index;
    private Storage storage;
    private ReentrantLock lock = new ReentrantLock();
    private Logger logger = Logger.getLogger(DatabaseImpl.class);
    private CompactionStrategy compactionStrategy;
    private long insertCount = 0L;


    public DatabaseImpl(Index index, Storage storage, Level logLevel, CompactionStrategy compactionStrategy) {
        this.index = index;
        this.storage = storage;
        this.compactionStrategy = compactionStrategy;
        logger.setLevel(logLevel);
        logger.info("Starting database");
        compact();
    }

    @Override
    public void put(String key, String value) {
        putHelper(key, value, false);
    }

    private void putHelper(String key, String value, boolean alreadyCompacted) {
        while (!lock.tryLock()) {
            lock.tryLock();
        }

        logger.trace("Locked database for writing");

        insertCount++;

        try {
            int hash = MurmurHash3.hash32x86((key + value).getBytes());
            byte[] log = getLogForKeyValue(hash, key, value);
            Optional<Long> offsetOptional = storage.persist(log);

            if (offsetOptional.isPresent()) {
                index.put(key, offsetOptional.get());
                logger.trace("Wrote " + key + "=" + value);
            } else {
                logger.warn(String.format("Couldn't persist %s=%s=%s", hash, key, value));
            }
        } finally {
            lock.unlock();
            logger.trace("Unlocked database for writing");
        }

        if (compactionStrategy.shouldCompact(insertCount) && !alreadyCompacted) {
            compact();
        }
    }

    @Override
    public Optional<String> get(String key) {
        while (!lock.tryLock()) {
            lock.tryLock();
        }

        logger.trace("Locked database for reading");

        try {
            Optional<Long> offset = index.get(key);

            /* TODO: Don't like the pattern below */
            if (offset.isPresent()) {
                Optional<byte[]> bytesOptional = storage.retrieve(offset.get());

                if (bytesOptional.isPresent()) {
                    return Optional.of(bytesToString(bytesOptional.get()));
                } else {
                    logger.warn("Couldn't retrieve bytes at offset");
                }
            } else {
                logger.warn(String.format("Couldn't find key %s in index", key)); /* TODO: make nicer */
            }
        } finally {
            lock.unlock();
            logger.trace("Unlocked database for reading");
        }

        return Optional.empty();
    }

    @Override
    public void remove(String key) {
        put(key, String.valueOf(EMPTY_VALUE));
    }

    @Override
    public ArrayList<String> range(String from, String to) {
        ArrayList<Long> offsets = index.range(from, to);
        ArrayList<String> values = new ArrayList<>();

        for (int i = 0; i < offsets.size(); i++) {
            Optional<byte[]> bytesOptional = storage.retrieve(offsets.get(i));
            bytesOptional.ifPresent(bytes -> values.add(bytesToString(bytes)));
        }

        return values;
    }

    @Override
    public ArrayList<String> all() {
        ArrayList<String> values = new ArrayList<>();

        for (Map.Entry<String, Long> entry : index.entrySet()) {
            long offset = entry.getValue();
            Optional<byte[]> bytesOptional = storage.retrieve(offset);
            bytesOptional.ifPresent(bytes -> values.add(bytesToString(bytes)));
        }

        return values;
    }

    private int extractHash(String string) {
        String[] strings = string.split(String.valueOf(DELIMITER));
        return Integer.parseInt(strings[1]);
    }

    private String extractKey(String string) {
        /* TODO: partial writes could end with corruption */
        String[] strings = string.split(String.valueOf(DELIMITER));
        return strings[2].trim();
    }

    private String extractValue(String string) {
        String[] strings = string.split(String.valueOf(DELIMITER));
        return strings[3].trim();
    }

    private byte[] getLogForKeyValue(int hash, String key, String value) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DELIMITER).append(hash).append(DELIMITER).append(key).append(DELIMITER).append(value).append(END_OF_LINE);
        return stringBuilder.toString().getBytes();
    }

    private String bytesToString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            stringBuilder.append((char) bytes[i]);
        }

        return extractValue(stringBuilder.toString());
    }

    private void compact() {
        logger.trace("Index before compaction " + index.entrySet().toString());

        while (!lock.tryLock()) {
            lock.tryLock();
        }

        logger.debug("Locked database for compaction");

        index.reset();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(storage.getPath()));
            HashSet<String> keysSeen = new HashSet<>();
            ArrayDeque<String> arrayDeque = new ArrayDeque<>();

            logger.debug("Reading keys into memory");

            while(bufferedReader.ready()) {
                arrayDeque.push(bufferedReader.readLine());
            }

            boolean reset = storage.reset();

            if (!reset) {
                throw new IllegalStateException("Couldn't reset storage");
            }

            logger.debug("Compacting keys");

            while (!arrayDeque.isEmpty()) {
                String line = arrayDeque.pop();
                String key = extractKey(line);

                if (!keysSeen.contains(key)) {
                    int hash = extractHash(line);
                    String value = extractValue(line);
                    putHelper(key, value, true);
                }
                keysSeen.add(key);
            }

        } catch (Exception e) {
            Error.handle("Error occurred while compacting", e);
        } finally {
            lock.unlock();
            logger.debug("Unlocked database for compaction");
            logger.trace("Index after compaction " + index.entrySet().toString());
        }
    }
}
