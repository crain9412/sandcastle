package com.jwcrain.sandcastle.database.storage;

import java.util.Optional;

public interface Storage {
    Optional<Long> persist(byte[] bytes);
    Optional<byte[]> retrieve(long offset);
    String getPath();
    boolean reset();
}
