package com.jwcrain.sandcastle.database.index;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/* TODO: is leaking locking info outside of the index a bad thing(tm)? */
public interface Index {
    void put(String key, long value);
    Optional<IndexValue> get(String key);
    boolean lock(String key);
    boolean unlock(String key);
    void remove(String key);
    boolean contains(String key);
    ArrayList<IndexValue> range(String from, String to);
    Set<Map.Entry<String, IndexValue>> entrySet();
    void reset();
}
