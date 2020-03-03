package com.jwcrain.sandcastle.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

public interface Database {
    void put(String key, String value);
    Optional<String> get(String key); /* Blocks */
    void remove(String key);
    ArrayList<String> range(String from, String to);
    Iterator<Map.Entry<String, Long>> iterator();
    boolean compact(); /* Blocks.  For testing, could also allow programmers to use a custom compaction strategy */
    long getInsertId(); /* No guar.s are attached to this, should only be used for testing */
}
