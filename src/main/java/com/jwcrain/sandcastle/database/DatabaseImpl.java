package com.jwcrain.sandcastle.database;

import com.jwcrain.sandcastle.database.index.Index;
import com.jwcrain.sandcastle.database.storage.Storage;
import org.apache.commons.codec.digest.MurmurHash3;

import java.util.ArrayList;
import java.util.Map;

public class DatabaseImpl implements Database {
    private static final char DELIMITER = '=';
    private static final char END_OF_LINE = '\n';
    private Index index;
    private Storage storage;

    public DatabaseImpl(Index index, Storage storage) {
        this.index = index;
        this.storage = storage;
        /* TODO: recreate from existing DB */
    }

    @Override
    public void put(String key, String value) {
        int hash = MurmurHash3.hash32x86((key + value).getBytes());
        long offset = storage.persist(getLogForKeyValue(hash, key, value));
        index.put(key, offset);
    }

    @Override
    public String get(String key) {
        long offset = index.get(key);
        byte[] bytes = storage.retrieve(offset);
        String diskValue = bytesToString(bytes);
        return bytesToString(bytes);
    }

    @Override
    public ArrayList<String> range(String from, String to) {
        ArrayList<Long> offsets = index.range(from, to);
        ArrayList<String> values = new ArrayList<>();

        for (int i = 0; i < offsets.size(); i++) {
            byte[] bytes = storage.retrieve(offsets.get(i));
            values.add(bytesToString(bytes));
        }

        return values;
    }

    @Override
    public ArrayList<String> all() {
        ArrayList<String> values = new ArrayList<>();

        for (Map.Entry<String, Long> entry : index.entrySet()) {
            long offset = entry.getValue();
            byte[] bytes = storage.retrieve(offset);
            values.add(bytesToString(bytes));
        }

        return values;
    }

    private int extractHashFromDiskValue(String string) {
        String[] strings = string.split(String.valueOf(DELIMITER));
        return Integer.valueOf(strings[0]);
    }

    private String extractValueFromDiskValue(String string) {
        String[] strings = string.split(String.valueOf(DELIMITER));
        return strings[2].trim();
    }

    private byte[] getLogForKeyValue(int hash, String key, String value) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(hash).append(DELIMITER).append(key).append(DELIMITER).append(value).append(END_OF_LINE);
        return stringBuilder.toString().getBytes();
    }

    private String bytesToString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            stringBuilder.append((char) bytes[i]);
        }

        return extractValueFromDiskValue(stringBuilder.toString());
    }
}
