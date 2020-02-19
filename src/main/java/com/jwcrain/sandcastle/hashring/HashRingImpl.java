package com.jwcrain.sandcastle.hashring;

import org.apache.commons.codec.digest.MurmurHash3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/*
    An implementation of HashRing that resolves collisions with linear probing
    We use 2 HashMaps, to store key -> degree and degree -> key in both directions for constant time access
    We use one TreeMap, to maintain an ordered list of degree -> key for efficient clockwise lookup
    Time complexities:
        O(log n) insert
        O(log n) remove
        O(1) clockwise
        O(1) access
 */
public class HashRingImpl<K> implements HashRing<K> {
    private static final int DEGREES_IN_A_CIRCLE = 360;
    private HashMap<Double, K> degreeToKeyMap = new HashMap<>();
    private TreeMap<Double, K> degreeToKeyTree = new TreeMap<>();
    private HashMap<K, Double> keyToDegreeMap = new HashMap<>();

    @Override
    public double put(K key) {
        return putHelper(hash(key.toString()), key);
    }

    @Override
    public Optional<K> get(K key) {
        return getHelper(hash(key.toString()), key);
    }

    @Override
    public void remove(K key) {
        double degree = keyToDegreeMap.get(key);
        keyToDegreeMap.remove(key);
        degreeToKeyMap.remove(degree);
        degreeToKeyTree.remove(degree);
    }

    @Override
    public Optional<K> clockwise(double degree) {
        double nextPossibleDouble = Math.nextUp(degree);
        Map.Entry<Double, K> potentialMatch = degreeToKeyTree.ceilingEntry(nextPossibleDouble);
        if (potentialMatch == null) {
            potentialMatch = degreeToKeyTree.ceilingEntry(degree - DEGREES_IN_A_CIRCLE);
        }
        return Optional.ofNullable(potentialMatch.getValue());
    }

    @Override
    public Optional<Double> getDegree(K key) {
        return Optional.ofNullable(keyToDegreeMap.get(key));
    }

    private int hash(String s) {
        return MurmurHash3.hash32x86(s.getBytes());
    }

    private double putHelper(int hash, K key) {
        double degree = getDegreeFromHash(hash);
        K potentialMatch = degreeToKeyMap.get(degree);

        if (potentialMatch != null && !potentialMatch.equals(key)) {
            putHelper(getNextPossibleHash(hash), key);
        }

        degreeToKeyMap.put(degree, key);
        degreeToKeyTree.put(degree, key);
        keyToDegreeMap.put(key, degree);
        return degree;
    }

    private Optional<K> getHelper(int hash, K key) {
        double degree = getDegreeFromHash(hash);
        K potentialMatch = degreeToKeyMap.get(degree);

        if (potentialMatch == null) {
            return Optional.empty();
        }

        if (potentialMatch.equals(key)) {
            return Optional.of(key);
        }

        return getHelper(getNextPossibleHash(hash), key);
    }

    private int getNextPossibleHash(int hash) {
        return (hash + 1) % Integer.MAX_VALUE;
    }

    private double getDegreeFromHash(int hash) {
        long positiveHash = (long) hash + Integer.MAX_VALUE; /* 0 - Integer.MAX_VALUE * 2 */
        double scalar = (double) positiveHash / ((long) Integer.MAX_VALUE * 2); /* 0 - 1 */
        return scalar * DEGREES_IN_A_CIRCLE; /* 0 - 360 */
    }
}
