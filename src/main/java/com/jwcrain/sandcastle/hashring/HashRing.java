package com.jwcrain.sandcastle.hashring;

import java.util.Optional;

public interface HashRing<K> {
    double put(K key);
    Optional<K> get(K key);
    Optional<K> clockwise(double degree);
}
