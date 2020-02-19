package com.jwcrain.sandcastle.consistenthash;

import java.util.*;

/*
    A class representing a server in our simulator, which has it's own in memory hash map
 */
public class Server<K, V> {
    private HashMap<K, V> hashMap = new HashMap<>();
    private final String name;

    public Server(String name) {
        this.name = name;
    }

    public void put(K key, V value) {
        hashMap.put(key, value);
    }

    public void remove(K key) {
        hashMap.remove(key);
    }

    public Optional<V> get(K key) {
        return Optional.ofNullable(hashMap.get(key));
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return hashMap.entrySet();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server<?, ?> server = (Server<?, ?>) o;
        return Objects.equals(name, server.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Server{" +
                "hashMap=" + hashMap +
                ", name='" + name + '\'' +
                '}';
    }
}
