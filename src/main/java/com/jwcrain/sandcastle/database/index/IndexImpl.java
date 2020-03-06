package com.jwcrain.sandcastle.database.index;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class IndexImpl implements Index {
    private HashMap<String, IndexValue> hashMap = new HashMap<>();
    private TreeMap<String, IndexValue> treeMap = new TreeMap<>();

    @Override
    public void put(String key, long offset) {
        hashMap.put(key, new IndexValue(offset));
        treeMap.put(key, new IndexValue(offset));
    }

    @Override
    public Optional<IndexValue> get(String key) {
        return Optional.ofNullable(hashMap.get(key));
    }

    @Override
    public boolean lock(String key) {
        Optional<IndexValue> indexValueOptional = get(key);

        if (!indexValueOptional.isPresent()) {
            return false;
        }

        IndexValue indexValue = indexValueOptional.get();

        ReentrantLock lock = indexValue.getLock();

        try {
            lock.tryLock(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean unlock(String key) {
        Optional<IndexValue> indexValueOptional = get(key);

        if (!indexValueOptional.isPresent()) {
            return false;
        }

        IndexValue indexValue = indexValueOptional.get();

        ReentrantLock lock = indexValue.getLock();

        lock.unlock();

        return true;
    }

    @Override
    public void remove(String key) {
        hashMap.remove(key);
        treeMap.remove(key);
    }

    @Override
    public boolean contains(String key) {
        return hashMap.containsKey(key);
    }

    @Override
    public ArrayList<IndexValue> range(String from, String to) {
        ArrayList<IndexValue> values = new ArrayList<>();
        Map.Entry<String, IndexValue> currentEntry = treeMap.higherEntry(from);

        while (currentEntry != null) {
            String key = currentEntry.getKey();

            if (key.compareTo(to) >= 0) {
                break;
            }
            values.add(currentEntry.getValue());
            currentEntry = treeMap.higherEntry(key);
        }

        return values;
    }

    @Override
    public Set<Map.Entry<String, IndexValue>> entrySet() {
        return treeMap.entrySet();
    }

    @Override
    public void reset() {
        hashMap = new HashMap<>();
        treeMap = new TreeMap<>();
    }
}
