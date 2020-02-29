package com.jwcrain.sandcastle.database.index;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public interface Index {
    void put(String key, long value);
    long get(String key);
    void remove(String key);
    boolean contains(String key);
    ArrayList<Long> range(String from, String to);
    Set<Map.Entry<String, Long>> entrySet();
    void reset();
}
