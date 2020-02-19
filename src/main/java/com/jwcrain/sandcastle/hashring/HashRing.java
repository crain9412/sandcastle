package com.jwcrain.sandcastle.hashring;

import java.util.Optional;

public interface HashRing<K> {
    double put(K key);
    Optional<Double> getDegree(K key);
    Optional<K> get(K key);
    void remove(K key);
    Optional<K> clockwise(double degree);
    Optional<K> counterClockwise(double degree);
}
