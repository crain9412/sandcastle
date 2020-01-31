package com.jwcrain.sandcastle.crainhamt;

import java.util.ArrayList;

/* Data storage class, must have an arraylist of pairs to avoid collisions
*  TODO: this could be an ArrayDeque, any reason to change?
*  */
public class Datum<K, V> {
    private ArrayList<KeyValuePair<K, V>> pairs;

    public Datum() {
        pairs = new ArrayList<>();
    }

    public void addPair(KeyValuePair<K, V> pair) {
        pairs.add(pair);
    }

    public ArrayList<KeyValuePair<K, V>> getPairs() {
        return pairs;
    }

    public void setPairs(ArrayList<KeyValuePair<K, V>> pairs) {
        this.pairs = pairs;
    }
}
