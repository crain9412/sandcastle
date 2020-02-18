package com.jwcrain.sandcastle.hashring;

import org.apache.commons.codec.digest.MurmurHash3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/*
    An implementation of HashRing that resolves collisions with linear probing
 */
public class HashRingImpl<K> implements HashRing<K> {
    private static final int DEGREES_IN_A_CIRCLE = 360;
    private HashMap<Double, K> degreeToKeyMap = new HashMap<>();
    private TreeMap<Double, K> degreeToKeyTree = new TreeMap<>();

    @Override
    public double put(K key) {
        return putHelper(hash(key.toString()), key);
    }

    @Override
    public Optional<K> get(K key) {
        return getHelper(hash(key.toString()), key);
    }

    @Override
    public Optional<K> clockwise(double degree) {
        double nextPossibleDouble = Math.nextAfter(degree, Double.POSITIVE_INFINITY);
        //System.out.printf("Attempting to find clockwise entry from degree %f15 with next possible double %f15\n", degree, nextPossibleDouble);
        //System.out.printf("Potential matches: %s\n", degreeToKeyTree);
        Map.Entry<Double, K> potentialMatch = degreeToKeyTree.ceilingEntry(nextPossibleDouble);
        //System.out.printf("Found potential match %s\n", potentialMatch);
        if (potentialMatch == null) {;
            potentialMatch = degreeToKeyTree.ceilingEntry(degree - DEGREES_IN_A_CIRCLE);
            //System.out.printf("Found potential match %s\n", potentialMatch);
        }
        //System.out.printf("Returning potential match %s\n", potentialMatch);
        return Optional.ofNullable(potentialMatch.getValue());
    }

    private int hash(String s) {
        return MurmurHash3.hash32x86(s.getBytes());
    }

    private double putHelper(int hash, K key) {
        double degree = getDegreeFromHash(hash);
        K potentialMatch = degreeToKeyMap.get(degree);

        //System.out.printf("Attempting to add key %s with hash %d at degree %f\n", key.toString(), hash, degree);

        if (potentialMatch != null && !potentialMatch.equals(key)) {
            //System.out.printf("Collision, %f already exists in our HashRing, new key: %s, existing key: %s, probing!\n", degree, key.toString(), potentialMatch.toString());
            putHelper(getNextPossibleHash(hash), key);
        }

        degreeToKeyMap.put(degree, key);
        degreeToKeyTree.put(degree, key);
        return degree;
    }

    private Optional<K> getHelper(int hash, K key) {
        double degree = getDegreeFromHash(hash);
        K potentialMatch = degreeToKeyMap.get(degree);

        //System.out.printf("Attempting to get key %s with hash %d at degree %f\n", key.toString(), hash, degree);

        if (potentialMatch == null) {
            return Optional.empty();
        }

        if (potentialMatch.equals(key)) {
            return Optional.of(key);
        }

        //System.out.printf("Collision, found existing key %s that doesn't match key %s, probing!\n", potentialMatch.toString(), key.toString());
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
