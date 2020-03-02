package com.jwcrain.sandcastle.database.index;

import java.util.*;

public class IndexImpl implements Index {
    private HashMap<String, Long> hashMap = new HashMap<>();
    private TreeMap<String, Long> treeMap = new TreeMap<>();

    @Override
    public void put(String key, long offset) {
        hashMap.put(key, offset);
        treeMap.put(key, offset);
    }

    @Override
    public Optional<Long> get(String key) {
        return Optional.ofNullable(hashMap.get(key));
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
    public ArrayList<Long> range(String from, String to) {
        ArrayList<Long> keys = new ArrayList<>();
        Map.Entry<String, Long> currentEntry = treeMap.ceilingEntry(from);

        while (currentEntry != null) {
            String key = currentEntry.getKey();

            if (key.compareTo(to) >= 0) {
                break;
            }
            keys.add(currentEntry.getValue());
            currentEntry = treeMap.ceilingEntry(key);
        }

        return keys;
    }

    @Override
    public Set<Map.Entry<String, Long>> entrySet() {
        return treeMap.entrySet();
    }

    @Override
    public void reset() {
        hashMap = new HashMap<>();
        treeMap = new TreeMap<>();
    }
}
