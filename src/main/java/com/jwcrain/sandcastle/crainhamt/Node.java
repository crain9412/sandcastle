package com.jwcrain.sandcastle.crainhamt;

import java.util.ArrayList;

public class Node<K, V> {
    /* This node may contain up to 64 children */
    private ArrayList<Node<K, V>> children = new ArrayList<>(HashArrayMappedTrie.STORAGE_PER_NODE_IN_BITS);

    /* This node may contain up to 64 data */
    private ArrayList<Datum<K, V>> data = new ArrayList<>(HashArrayMappedTrie.STORAGE_PER_NODE_IN_BITS);

    public Node() {
        for (int i = 0; i < HashArrayMappedTrie.STORAGE_PER_NODE_IN_BITS; i++) {
            children.add(null);
            data.add(new Datum<>());
        }
    }

    public boolean addressOccupied(int address) {
        return data.get(address) != null;
    }

    public KeyValuePair<K, V> getKeyValuePair(int address, K key) {
        ArrayList<KeyValuePair<K, V>> pairs = data.get(address).getPairs();

        for (int i = 0; i < pairs.size(); i++) {
            if (pairs.get(i).getKey().equals(key)) {
                return pairs.get(i);
            }
        }

        return null;
    }

    public void setKeyValuePair(int address, KeyValuePair<K, V> pair) {
        Datum<K, V> datum = data.get(address);
        ArrayList<KeyValuePair<K, V>> pairs = datum.getPairs();

        for (int i = 0; i < pairs.size(); i++) {
            if (pairs.get(i).getKey().equals(pair.getKey())) {
                /* Overwrite */
                pairs.set(i, pair);
                return;
            }
        }

        datum.addPair(pair);
    }

    public ArrayList<Datum<K, V>> getData() {
        return data;
    }

    public void setData(ArrayList<Datum<K, V>> data) {
        this.data = data;
    }

    public ArrayList<Node<K, V>> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Node<K, V>> children) {
        this.children = children;
    }

    public Node<K, V> getChildNode(int address) {
        return children.get(address);
    }

    public void setChildNode(int address, Node<K, V> childNode) {
        children.set(address, childNode);
    }
}