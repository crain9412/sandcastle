package com.jwcrain.sandcastle.database;

import java.util.ArrayList;
import java.util.Optional;

public interface Database {
    void put(String key, String value);
    Optional<String> get(String key);
    void remove(String key);
    ArrayList<String> range(String from, String to);
    ArrayList<String> all();
}
