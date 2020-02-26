package com.jwcrain.sandcastle.storage;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class StorageImpl {
    private static final int INT_SIZE_BYTES = Integer.SIZE / 8;
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

    public synchronized long persist(byte[] bytes) {
        try {
            int writeSize = bytes.length + INT_SIZE_BYTES;
            ByteBuffer byteBuffer = ByteBuffer.allocate(writeSize);
            byteBuffer.putInt(writeSize);
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

    public byte[] retrieve(long offset) {
        try {
            ByteBuffer sizeBuffer = ByteBuffer.allocate(INT_SIZE_BYTES);
            fileChannel.read(sizeBuffer, offset);
            sizeBuffer.flip();
            int readSize = sizeBuffer.getInt();
            ByteBuffer byteBuffer = ByteBuffer.allocate(readSize - INT_SIZE_BYTES);
            fileChannel.read(byteBuffer, offset + INT_SIZE_BYTES);
            byteBuffer.flip();
            return byteBuffer.array();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
