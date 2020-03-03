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
import java.util.concurrent.*;

import static com.jwcrain.sandcastle.database.concurrency.Helper.waitForFuture;

public class DatabaseImpl implements Database {
    private static final char DELIMITER = '=';
    private static final char END_OF_LINE = '\n';
    private static final char EMPTY_VALUE = ' ';
    private static final int ID_INDEX = 1;
    private static final int HASH_INDEX = 2;
    private static final int KEY_INDEX = 3;
    private static final int VALUE_INDEX = 4;
    private Index index;
    private Storage storage;
    private Logger logger = Logger.getLogger(DatabaseImpl.class);
    private CompactionStrategy compactionStrategy;
    private long insertId = Long.MIN_VALUE; /* Monotonically increasing */
    private ExecutorService writeExecutorService = Executors.newSingleThreadExecutor();
    private ExecutorService readExecutorService;

    /* TODO: use builder pattern */
    public DatabaseImpl(Index index, Storage storage, Level logLevel, CompactionStrategy compactionStrategy, int readThreads
    ) {
        this.index = index;
        this.storage = storage;
        this.compactionStrategy = compactionStrategy;
        logger.setLevel(logLevel);
        logger.info("Starting database");
        this.readExecutorService = Executors.newFixedThreadPool(readThreads);
        writeExecutorService.submit(this::compact);
    }

    @Override
    public void put(String key, String value) {
        writeExecutorService.submit(() -> putHelper(key, value, false));
    }

    private String putHelper(String key, String value, boolean alreadyCompacted) {
        insertId++;

        try {
            int hash = MurmurHash3.hash32x86((key + value).getBytes());
            byte[] log = getLogForKeyValue(insertId, hash, key, value);
            Optional<Long> offsetOptional = storage.persist(log);

            if (offsetOptional.isPresent()) {
                index.put(key, offsetOptional.get());
                logger.trace("Wrote " + key + "=" + value);
            } else {
                logger.warn(String.format("Couldn't persist %s=%s=%s=%s", insertId, hash, key, value));
                return null;
            }
        } finally {
            logger.trace("Unlocked database for writing");
        }

        if (compactionStrategy.shouldCompact(insertId) && !alreadyCompacted) {
            compactHelper();
        }

        return value;
    }

    @Override
    public Optional<String> get(String key) {
        try {
            return readExecutorService.submit(() -> getHelper(key)).get();
        } catch (Exception e) {
            Error.handle("Error while executing read", e);
        }
        return Optional.empty();
    }

    private Optional<String> getHelper(String key) {
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

        return Optional.empty();
    }

    @Override
    public void remove(String key) {
        put(key, String.valueOf(EMPTY_VALUE));
    }

    @Override
    public ArrayList<String> range(String from, String to) {
        Future<ArrayList<String>> future = readExecutorService.submit(() -> rangeHelper(from, to));

        return waitForFuture(future);
    }

    private ArrayList<String> rangeHelper(String from, String to) {
        ArrayList<Long> offsets = index.range(from, to);
        ArrayList<String> values = new ArrayList<>();

        for (int i = 0; i < offsets.size(); i++) {
            Optional<byte[]> bytesOptional = storage.retrieve(offsets.get(i));
            bytesOptional.ifPresent(bytes -> values.add(bytesToString(bytes)));
        }

        return values;
    }

    @Override
    public Iterator<Map.Entry<String, Long>> iterator() {
        return index.entrySet().iterator();
    }

    private long extractId(String string) {
        String[] strings = string.split(String.valueOf(DELIMITER));
        return Integer.parseInt(strings[ID_INDEX]);
    }

    private int extractHash(String string) {
        String[] strings = string.split(String.valueOf(DELIMITER));
        return Integer.parseInt(strings[HASH_INDEX]);
    }

    private String extractKey(String string) {
        /* TODO: partial writes could end with corruption */
        String[] strings = string.split(String.valueOf(DELIMITER));
        return strings[KEY_INDEX].trim();
    }

    private String extractValue(String string) {
        String[] strings = string.split(String.valueOf(DELIMITER));
        return strings[VALUE_INDEX].trim();
    }

    private byte[] getLogForKeyValue(long insertCount, int hash, String key, String value) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(DELIMITER)
                .append(insertCount).append(DELIMITER)
                .append(hash).append(DELIMITER)
                .append(key).append(DELIMITER)
                .append(value).append(END_OF_LINE);

        return stringBuilder.toString().getBytes();
    }

    private String bytesToString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            stringBuilder.append((char) bytes[i]);
        }

        return extractValue(stringBuilder.toString());
    }

    @Override
    public boolean compact() {
        try {
            return writeExecutorService.submit(this::compactHelper).get();
        } catch (Exception e) {
            Error.handle("Exception occurred while compacting", e);
        }
        return false;
    }

    private boolean compactHelper() {
        logger.trace("Index before compaction " + index.entrySet().toString());

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
            return false;
        } finally {
            logger.trace("Index after compaction " + index.entrySet().toString());
        }

        return true;
    }

    @Override
    public long getInsertId() {
        return insertId;
    }
}
