package com.jwcrain.sandcastle.crainlsmtree;

import com.jwcrain.sandcastle.appendonlylog.Log;
import com.jwcrain.sandcastle.database.storage.StorageImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/* TODO: do everything in binary... */
public class LSMTreeImpl {
    private static String ENTRY = "E";
    private static String FILE = "F";
    private TreeMap<String, String> treeMap = new TreeMap<>(); /*TODO: generify */
    private int maxTreeSize = 2048;
    private ReentrantLock lock = new ReentrantLock();
    private Log log = new Log("/tmp/lsm.log");
    StorageImpl storage = new StorageImpl("/tmp/lsm.bin");
    ArrayList<BufferedWriter> bufferedWriters = new ArrayList<>();

    public LSMTreeImpl() {
        lock.lock();
        BufferedReader bufferedReader = log.getReader();
        try {
            while (bufferedReader.ready()) {
                extractFromLine(bufferedReader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }

    private void extractFromLine(String line) {
        String[] strings = line.split("=");
        String command = strings[0].substring(0, 1);

        if (command.equals(ENTRY)) {
            String key = strings[0].substring(1);
            String value = strings[1];
            insert(key, value);
        } else if (command.equals(FILE)) {
            String path = strings[1];
            try {
                bufferedWriters.add(new BufferedWriter(new FileWriter(new File(path))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalStateException("Couldn't parse append only log");
        }
    }

    public void insert(String key, String value) {
        String logEntry = ENTRY + key + "=" + value + "\n";
        boolean logged = log.append(logEntry);
        if (logged) {
            treeMap.put(key, value);
            if (treeMap.size() > maxTreeSize) {
                flushToDisk();
            }
        } else {
            throw new IllegalStateException("Couldn't append to log, rolling back change");
        }
    }

    public String get(String key) {
        String possibleValue = treeMap.get(key);
        if (possibleValue == null) {
            for (BufferedWriter bufferedWriter : bufferedWriters) {

            }
        }
        return possibleValue;
    }

    private void flushToDisk() {
        lock.lock();

        try {
            String path = "/tmp/lsm/" + bufferedWriters.size() + ".txt";
            File file = new File(path);
            boolean createdPath = file.mkdirs();
            if (createdPath) {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(path)));
                String logEntry = FILE + "path=" + path;
                boolean logged = log.append(logEntry);
                if (logged) {
                    bufferedWriters.add(bufferedWriter);

                    for (Map.Entry<String, String> entry : treeMap.entrySet()) {
                        bufferedWriter.write(entry.getKey() + "=" + entry.getValue() + "\n");
                        bufferedWriter.flush();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        lock.unlock();
    }
}
