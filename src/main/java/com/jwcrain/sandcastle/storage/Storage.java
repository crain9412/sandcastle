package com.jwcrain.sandcastle.storage;

public interface Storage {
    long persist(byte[] bytes);
    byte[] retrieve(long offset);
}
