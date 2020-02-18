//package com.jwcrain.sandcastle.crainhamt;
//
//import java.util.ArrayList;
//
///*
//A hash array mapped trie implementation, similar to the one described here:
//https://idea.popcount.org/2012-07-2ADDRESS_SEGMENT_SIZE-introduction-to-hamt/idealhashtrees.pdf
//One difference is I'm storing 2 4 byte words in each node, and addresses are one byte in size so it divides nicely
//The upshot of this is it has greater memory usage per node and more fan out, so it should resolve faster.
//Should be more optimized for 64 bit processors.
//Maximum capacity is as follows:
//Level one: 64 elements
//Level two: 64 ^ 4 elements
//Level three: 64 ^ 8 elements
//Level four: 64 ^ 12 elements
// */
//public class HashArrayMappedTrie<K, V> {
//    private Node<K, V> root;
//    public static int STORAGE_PER_NODE_IN_BITS = 64;
//    public static int ADDRESS_SEGMENT_SIZE = 8;
//
//    public HashArrayMappedTrie() {
//        root = new Node<>();
//    }
//
//    public void put(K key, V value) {
//        System.out.printf("Putting %s=%s\n", key, value);
//
//        int hash = key.hashCode();
//        System.out.printf("Hashed key %s, got hash %s\n", key, hash);
//
//        putHelper(root, hash, new KeyValuePair<>(key, value),0, ADDRESS_SEGMENT_SIZE, 1);
//    }
//
//    public void putHelper(Node<K, V> node, int hash, KeyValuePair<K, V> pair, int start, int end, int depth) {
//        if (depth > STORAGE_PER_NODE_IN_BITS / ADDRESS_SEGMENT_SIZE) {
//            throw new IllegalStateException("HMAT Full");
//        }
//
//        int currentAddress = getBits(hash, start, end);
//        System.out.printf("Got address from %d to %d: %d\n", start, end, currentAddress);
//
//        if (!node.addressOccupied(currentAddress)) {
//            System.out.printf("No value exists at address, inserting %s at address %s in node %s\n", pair, currentAddress, node);
//            node.setKeyValuePair(currentAddress, pair);
//        } else {
//            KeyValuePair<K, V> existingPair = node.getKeyValuePair(currentAddress);
//
//            System.out.printf("Collision at address %d between %s and %s, we need to create a new node\n", currentAddress, pair, existingPair);
//            int otherHash = existingPair.getKey().hashCode();
//
//            if (pair.getKey().equals(existingPair.getKey())) {
//                /* Overwrite */
//                System.out.println("Same key, overwriting");
//                node.setKeyValuePair(currentAddress, pair);
//                return;
//            }
//            Node<K, V> createdNode = new Node<>();
//            int nextStart = start + ADDRESS_SEGMENT_SIZE;
//            int nextEnd = end + ADDRESS_SEGMENT_SIZE;
//            putHelper(createdNode, hash, pair, nextStart, nextEnd, depth + 1);
//            putHelper(createdNode, otherHash, existingPair, nextStart, nextEnd, depth + 1);
//            node.setChildNode(currentAddress, createdNode);
//        }
//    }
//
//    public V get(K key) {
//        return getHelper(root, key.hashCode(), key, 0, ADDRESS_SEGMENT_SIZE, 1);
//    }
//
//    public V getHelper(Node<K, V> node, int hash, K key, int start, int end, int depth) {
//        if (depth > STORAGE_PER_NODE_IN_BITS / ADDRESS_SEGMENT_SIZE) {
//            return null;
//        }
//
//        int currentAddress = getBits(hash, start, end);
//        System.out.printf("Got address from %d to %d: %d\n", start, end, currentAddress);
//
//        System.out.printf("Trying to find key %s at address %s\n", key, currentAddress);
//        KeyValuePair<K, V> existingPair = node.getKeyValuePair(currentAddress, key);
//
//        if (existingPair == null) {
//            return null;
//        }
//
//        if (existingPair.getKey().equals(key)) {
//            System.out.println("Found it");
//            return existingPair.getValue();
//        }
//
//        //System.out.printf("Must've had a collision, recursing to Node %s\n", node);
//        Node<K, V> nextNode = node.getChildNode(currentAddress);
//        return getHelper(nextNode, hash, key,start + ADDRESS_SEGMENT_SIZE, end + ADDRESS_SEGMENT_SIZE, depth + 1);
//    }
//
//    /* O(1) */
//    public static int getBits(int number, int start, int end) {
//        boolean[] bitArray = new boolean[ADDRESS_SEGMENT_SIZE];
//
//        //System.out.printf("Evaluating %s from %d to %d\n", Integer.toBinaryString(number), start, end);
//
//        for (int i = start; i < end; i++) {
//            bitArray[i - start] = bitwiseGet(number, i);
//        }
//
//        return bitArrayToInt(bitArray);
//    }
//
//    /* O(1) */
//    public static int bitArrayToInt(boolean[] bytes) {
//        int decimalRepresentation = 2 ^ ADDRESS_SEGMENT_SIZE;
//        int answer = 0;
//
//        for (int i = 0; i < ADDRESS_SEGMENT_SIZE; i++) {
//            if (bytes[i]) {
//                answer += decimalRepresentation;
//            }
//            decimalRepresentation /= 2;
//        }
//
//        return answer;
//    }
//
//    /* O(1) */
//    public static boolean bitwiseGet(Integer number, Integer position) {
//        //System.out.printf("Decimal = %d, Binary = %s; Mask = %s, Set = %b\n", number, Integer.toBinaryString(number), Integer.toBinaryString(1 << position), (number & (1 << position)) != 0);
//        return (number & (1 << position)) != 0;
//    }
//
//    @Override
//    public String toString() {
//        return traverse(root);
//    }
//
//    private String traverse(Node<K, V> currentNode) {
//        String s = "";
//        s += "Node: " + currentNode.toString() + ";\n";
//        s += "Data: ";
//        for (int i = 0; i < currentNode.getData().size(); i++) {
//            Datum<K, V> datum = currentNode.getData().get(i);
//            if (datum != null && !datum.getPairs().isEmpty()) {
//                s += "[" + datum.getPairs() + "]";
//            }
//        }
//        for (int i = 0; i < currentNode.getChildren().size(); i++) {
//            Node<K, V> child = currentNode.getChildren().get(i);
//            if (child != null) {
//                s += traverse(child);
//            }
//        }
//        return s;
//    }
//}