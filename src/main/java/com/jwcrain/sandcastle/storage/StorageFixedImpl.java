package com.jwcrain.sandcastle.storage;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class StorageFixedImpl implements Storage {
    private FileChannel fileChannel;
    private long offset = 0;
    private int readSize;

    public StorageFixedImpl(String path, int readSize) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
            fileChannel = randomAccessFile.getChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.readSize = readSize;
    }

    @Override
    public synchronized long persist(byte[] bytes) {
        try {
            int writeSize = bytes.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(writeSize);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            offset += writeSize;
            return offset - writeSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public byte[] retrieve(long offset) {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(readSize);
            fileChannel.read(byteBuffer, offset + readSize);
            byteBuffer.flip();
            return byteBuffer.array();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
