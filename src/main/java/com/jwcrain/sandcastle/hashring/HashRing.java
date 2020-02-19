package com.jwcrain.sandcastle.hashring;

import java.util.Optional;

/*
    HashRing represents an abstract ring used for @see <a href="https://en.wikipedia.org/wiki/Consistent_hashing">consistent hashing</a>
    It provides the following functions:
        - Put a key into the HashRing and return the *unique* degree associated with it
        - Get the degree of a key in the HashRing
        - Get the key if it exists from the HashRing
        - Remove a key from the HashRing
        - Get the closest clockwise key to a given degree in the HashRing
 */
public interface HashRing<K> {
    double put(K key);
    Optional<Double> getDegree(K key);
    Optional<K> get(K key);
    void remove(K key);
    Optional<K> clockwise(double degree);
}
