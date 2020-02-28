package com.jwcrain.sandcastle.database.storage;

public interface Storage {
    long persist(byte[] bytes);
    byte[] retrieve(long offset);
}
