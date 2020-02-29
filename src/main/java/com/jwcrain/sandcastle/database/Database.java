package com.jwcrain.sandcastle.database;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public interface Database {
    void put(String key, String value);
    String get(String key);
    void remove(String key);
    ArrayList<String> range(String from, String to);
    ArrayList<String> all();
}
