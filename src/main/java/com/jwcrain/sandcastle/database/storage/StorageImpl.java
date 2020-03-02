package com.jwcrain.sandcastle.database.storage;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import com.jwcrain.sandcastle.database.error.Error;

public class StorageImpl implements Storage {
    private static final int INT_SIZE_BYTES = Integer.SIZE / 8;
    private Logger logger = Logger.getLogger(StorageImpl.class);
    private FileChannel fileChannel;
    private long offset = 0;
    private String path;
    private RandomAccessFile randomAccessFile;

    public StorageImpl(String path) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(path, "rw");
            fileChannel = randomAccessFile.getChannel();
            this.randomAccessFile = randomAccessFile;
        } catch (Exception e) {
            Error.handle("Error occurred while opening random access file", e);
        }
        this.path = path;
    }

    @Override
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
            Error.handle("Error occurred while persisting bytes", e);
        }
        return 0L;
    }

    @Override
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
            Error.handle("Error occurred while retrieving offset", e);
        }
        return null;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void reset() {
        /* TODO: handle crashes during reset */
        try {
            randomAccessFile.setLength(0L);
        } catch (Exception e) {
            Error.handle("Error occurred while setting file length", e);
        }
        this.offset = 0L;
    }

    @Override
    public long getSize() {
        try {
            File file = new File(path);
            return file.length();
        } catch (Exception e) {
            Error.handle("Error occurred while getting file size", e);
        }
        return -1L;
    }
}
