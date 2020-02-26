package com.jwcrain.sandcastle.database;

import com.jwcrain.sandcastle.storage.Storage;
import com.jwcrain.sandcastle.storage.StorageFixedImpl;
import com.jwcrain.sandcastle.storage.StorageVariableImpl;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class DatabaseImpl<K, V extends Serializable> {
    private static final int LONG_SIZE_BYTES = Long.SIZE / 8;
    private HashMap<K, Long> index = new HashMap<>();
    private Storage dataStorage;
    private Storage indexStorage;

    public DatabaseImpl(String path, Storage storage) {
        this.dataStorage = storage;
        this.indexStorage = new StorageFixedImpl(path, LONG_SIZE_BYTES * 2);
        buildIndex();
    }

    private void buildIndex() {
        long offset = 0L;
        byte[] bytes = indexStorage.retrieve(offset);
        while (bytes.length != 0) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            byteBuffer.flip();
            K key = (K) byteBuffer.getLong();
            long offset = (K) byteBuffer.getLong();
            put(key, )
            offset += bytes.length + StorageVariableImpl.INT_SIZE_BYTES;
            bytes = indexStorage.retrieve(offset);
        }
    }

    private byte[] serializeIndex(K key, Long offset) {

    }

    public void put(K key, V value) {
        long offset = dataStorage.persist(value.toString().getBytes());
        indexStorage.persist(key.toString().getBytes());
        index.put(key, offset);
    }

    public V get(K key) {
        long offset = index.get(key);
        return (V) dataStorage.retrieve(offset);
    }
}
