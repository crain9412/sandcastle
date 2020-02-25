package com.jwcrain.sandcastle.storage;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class StorageImpl {
    private FileChannel fileChannel;
    private long offset = 0;

    public StorageImpl(String path) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
            fileChannel = randomAccessFile.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StorageData persist(byte[] bytes) {
        try {
            int writeSize = bytes.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(writeSize);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            offset += writeSize;
            return new StorageData(offset - writeSize, writeSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] retrieve(StorageData storageData) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(storageData.getSize());
            fileChannel.read(byteBuffer, storageData.getOffset());
            return byteBuffer.array();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
