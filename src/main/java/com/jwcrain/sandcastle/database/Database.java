package com.jwcrain.sandcastle.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public interface Database {
    void put(String key, String value);
    Optional<String> get(String key);
    void remove(String key);
    ArrayList<String> range(String from, String to);
    Iterator<Map.Entry<String, Long>> iterator();
    void compact(); /* For testing, could also allow programmers to use a custom compaction strategy */
}
