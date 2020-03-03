package com.jwcrain.sandcastle;

import com.jwcrain.sandcastle.consistenthash.Cluster;
import com.jwcrain.sandcastle.crainhashmap.Map;
import com.jwcrain.sandcastle.crainlsmtree.LSMTreeImpl;
import com.jwcrain.sandcastle.database.Database;
import com.jwcrain.sandcastle.database.DatabaseImpl;
import com.jwcrain.sandcastle.database.compactionstrategy.NoOpCompactionStrategy;
import com.jwcrain.sandcastle.database.compactionstrategy.RandomizedCompactionStrategy;
import com.jwcrain.sandcastle.database.index.Index;
import com.jwcrain.sandcastle.database.index.IndexImpl;
import com.jwcrain.sandcastle.database.storage.Storage;
import com.jwcrain.sandcastle.hashring.HashRingImpl;
import com.jwcrain.sandcastle.database.storage.StorageImpl;
import org.apache.log4j.Level;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class AppTest {
    private static final String NOT_FOUND = "NOT_FOUND";

    @Test
    public void testQuicksort() {
        int[] array = {8, 4, 5, 2, 3, 8, 7, 1, 9};
        int[] expected = {1, 2, 3, 4, 5, 7, 8, 8, 9};

        Quicksort.sort(array);

        assertArrayEquals(expected, array);
    }

    @Test
    public void testMergesort() {
        int[] array = {8, 4, 5, 2, 3, 8, 7, 1, 9};
        int[] expected = {1, 2, 3, 4, 5, 7, 8, 8, 9};

        array = Mergesort.sort(array);

        assertArrayEquals(expected, array);
    }

    @Test
    public void testHeapsort() {
        int[] array = {8, 4, 5, 2, 3, 8, 7, 1, 9};
        int[] expected = {1, 2, 3, 4, 5, 7, 8, 8, 9};

        Heapsort.sort(array);

        assertArrayEquals(expected, array);
    }

    @Test
    public void frequencyAnalysis() {
        String document = "Hello I'm a dog.  I'm a golden retriever.  Testing frequency.  Th./is should match this.";

        DocumentFrequency documentFrequency = new DocumentFrequency(document);

        Integer thisFrequency = documentFrequency.search("this");

        assertTrue(thisFrequency == 2);
    }
    @Test
    public void testCrainHashMap() {
        Map map = new Map();

        map.put("test1", "hello");
        map.put("test2", "world");
        map.put("test3", "crain");

        assertNotNull(map.get("test1"));
        assertTrue(map.get("test3").equals("crain"));
    }

//    @Test
//    public void testKnapsack() {
//        int[] weights = {4, 2, 3, 9, 7};
//        int[] values = {10, 100, 20, 1000, 50};
//        Knapsack knapsack = new Knapsack(5);
//        int maxValue = knapsack.solve(weights, values, 11);
//        System.out.println(maxValue);
//    }

    @Test
    public void testFibonacci() {
        BigInteger answer = Fibonacchi.calculate(100);
        assertTrue(answer.equals(new BigInteger("158456325028528675187087900672")));
    }

    @Test
    public void palindromeDetector() {
        assertTrue(Palindrome.detect("tacocat"));
        assertFalse(Palindrome.detect("tacocats"));
        assertTrue(Palindrome.detect("tacoocat"));
    }

    @Test
    public void testHashRing() {
        HashRingImpl<String> hashRing = new HashRingImpl<>();

        /*
        Attempting to add key 1 with hash -1810453357 at degree 28.249553
        Attempting to add key 2 with hash 19522071 at degree 181.636321
        Attempting to add key 3 with hash 264741300 at degree 202.190359
        Attempting to add key beta with hash 2022730153 at degree 349.543283
        Attempting to add key charlie with hash -481950697 at degree 139.603359
        Attempting to add key entry1 with hash 1257347571 at degree 285.389656
        Attempting to add key entry2 with hash 191393214 at degree 196.042394
        Attempting to add key entry3 with hash -2119187441 at degree 2.371761
         */

        hashRing.put("1");
        hashRing.put("2");
        hashRing.put("3");
        double highestDegree = hashRing.put("beta");
        hashRing.put("charlie");
        hashRing.put("entry1");
        hashRing.put("entry2");
        double lowestDegree = hashRing.put("entry3");

        assertEquals("beta", hashRing.get("beta").orElse("NOT FOUND"));
        assertEquals("entry3", hashRing.get("entry3").orElse("NOT FOUND"));
        assertEquals("1", hashRing.clockwise(lowestDegree).orElse("NOT FOUND"));
        assertEquals("entry3", hashRing.clockwise(highestDegree).orElse("NOT FOUND"));
    }

    @Test
    public void testHashRingCollisionResolution() {
        HashRingImpl<String> hashRing = new HashRingImpl<>();

        for (int i = 0; i < 100000; i++) {
            hashRing.put(Integer.toString(i));
            assertEquals(hashRing.get(Integer.toString(i)).orElse("NOT FOUND"), Integer.toString(i));
        }
    }

    @Test
    public void testConsistentHashing() {
        Cluster cluster = new Cluster("alpha");
        cluster.put("hello", "world");
        cluster.addServer("beta");
        cluster.put("test", "something");
        cluster.addServer("charlie");
        cluster.put("another", "key");
        cluster.addServer("delta");
        cluster.addServer("epsilon");
        cluster.removeServer("charlie");
        cluster.put("yet", "another");

        assertEquals("another", cluster.get("yet").orElse("NOT FOUND"));
        assertEquals("world", cluster.get("hello").orElse("NOT FOUND"));
        assertEquals("something", cluster.get("test").orElse("NOT FOUND"));
        assertEquals("key", cluster.get("another").orElse("NOT FOUND"));
        assertEquals("another", cluster.get("yet").orElse("NOT FOUND"));
    }

    @Test
    public void testConsistentHashingCollisionResolution() {
        Cluster cluster = new Cluster("alpha");

        for (int i = 0; i < 100; i++) {
            cluster.addServer(Integer.toString(i));
        }

        for (int i = 0; i < 100000; i++) {
            cluster.put(Integer.toString(i), Integer.toString(i * 2));
            assertEquals(cluster.get(Integer.toString(i)).orElse("NOT FOUND"), Integer.toString(i * 2));
        }

        for (int i = 100; i < 110; i++) {
            cluster.addServer(Integer.toString(i));
        }

        for (int i = 100001; i < 100100; i++) {
            cluster.put(Integer.toString(i), Integer.toString(i * 2));
            assertEquals(cluster.get(Integer.toString(i)).orElse("NOT FOUND"), Integer.toString(i * 2));
        }

        for (int i = 0; i < 10; i++) {
            cluster.removeServer(Integer.toString(i));
        }

        for (int i = 100100; i < 200000; i++) {
            cluster.put(Integer.toString(i), Integer.toString(i * 2));
            assertEquals(cluster.get(Integer.toString(i)).orElse("NOT FOUND"), Integer.toString(i * 2));
        }
    }

    @Test
    @Ignore
    public void storageTest() {
        StorageImpl storage = new StorageImpl("/tmp/test");
        byte[] bytes = new byte[]{0x48, 0x65, 0x6C, 0x6C, 0x6F};
        Optional<Long> offsetOptional = storage.persist(bytes);
        Random random = new Random();
        random.setSeed(31L); /* Make test repeatable */

        assertTrue(offsetOptional.isPresent());

        for (int i = 0; i < bytes.length; i++) {
            Optional<byte[]> bytesOptional = storage.retrieve(offsetOptional.get());
            assertTrue(bytesOptional.isPresent());
            assertEquals(bytes[i], bytesOptional.get()[i]);
        }

        /* Write about 40M of data */
        for (int i = 0; i < 10; i++) {
            int randomSize = random.nextInt(4000000) + 1; /* 4 MB */
            byte[] randomBytes = new byte[randomSize];

            for (int j = 0; j < randomBytes.length; j++) {
                char randomAlphanumericChar = (char) (random.nextInt(26) + 97);
                randomBytes[j] = (byte) randomAlphanumericChar;
            }

            Optional<Long> randomOffsetOptional = storage.persist(randomBytes);

            assertTrue(randomOffsetOptional.isPresent());

            Optional<byte[]> retrievedBytesOptional = storage.retrieve(randomOffsetOptional.get());

            assertTrue(retrievedBytesOptional.isPresent());

            for (int j = 0; j < randomBytes.length; j++) {
                assertEquals(randomBytes[j], retrievedBytesOptional.get()[j]);
            }
        }
    }

    @Test
    public void lsmTest() {
        LSMTreeImpl lsmTree = new LSMTreeImpl();
        lsmTree.insert("Hello", "World");
        lsmTree.insert("Test", "Entry");

        for (int i = 0; i < 10000; i++) {
            lsmTree.insert(Integer.toString(i), Integer.toString(i * 2));
            assertEquals(Integer.toString(i * 2), lsmTree.get(Integer.toString(i)));
        }
    }

    @Test
    public void databaseTest() {
        Storage storage = new StorageImpl("/tmp/db");
        Index index = new IndexImpl();
        Database database = new DatabaseImpl(index, storage, Level.INFO, new RandomizedCompactionStrategy());

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        Future<Boolean> future = executorService.submit(() -> {
            database.put("Hello", "World");
            assertEquals("World", database.get("Hello").orElse(NOT_FOUND));
            database.remove("Hello");
            assertEquals("", database.get("Hello").orElse(NOT_FOUND));
            database.put("Hello", "Jon");
            assertEquals("Jon", database.get("Hello").orElse(NOT_FOUND));
            return true;
        });

        waitForFuture(future);
    }

    @Ignore
    @Test
    public void databaseLoadTest() {
        Storage storage = new StorageImpl("/tmp/db");
        Index index = new IndexImpl();
        Database database = new DatabaseImpl(index, storage, Level.INFO, new RandomizedCompactionStrategy());
        long startNanos = System.nanoTime();

        ExecutorService executorService = Executors.newFixedThreadPool(16); /* Set equal to log. proc. count */

        Future<Boolean> future = executorService.submit(() -> {
            for (int i = 0; i < 250000; i++) {
                String s = Integer.toString(i);
                database.put("Hello", s);
                assertEquals(s, database.get("Hello").orElse(NOT_FOUND));
            }
            return true;
        });

        waitForFuture(future);

        double secondsElapsed = (double)(System.nanoTime() - startNanos) / 1000000000d;

        System.out.printf("Single Key Insert Count = 1,000,003;Seconds elapsed=%f;Inserts per second=%f\n", secondsElapsed, (1000003 / secondsElapsed));

        startNanos = System.nanoTime();

        future = executorService.submit(() -> {
            for (int i = 0; i < 1000; i++) {
                String key = Integer.toString(i);

                for (int j = 0; j < 1000; j++) {
                    String value = Integer.toString(j * 2);
                    database.put(key, value);
                    assertEquals(value, database.get(key).orElse(NOT_FOUND));
                }
            }
            return true;
        });

        waitForFuture(future);

        secondsElapsed = (double)(System.nanoTime() - startNanos) / 1000000000d;

        System.out.printf("Thousand Key Insert Count = 1M; Seconds elapsed=%f; Inserts per second=%f\n", secondsElapsed, (1000000 / secondsElapsed));

        startNanos = System.nanoTime();

        future = executorService.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                String key = Integer.toString(i);

                for (int j = 0; j < 10; j++) {
                    String value = Integer.toString(j * 2);
                    database.put(key, value);
                    assertEquals(value, database.get(key).orElse(NOT_FOUND));
                }
            }
            return true;
        });

        waitForFuture(future);

        secondsElapsed = (double)(System.nanoTime() - startNanos) / 1000000000d;

        System.out.printf("Hundred Thousand Key Insert Count = 1M; Seconds elapsed=%f; Inserts per second=%f\n", secondsElapsed, (1000000d / secondsElapsed));

        startNanos = System.nanoTime();

        future = executorService.submit(() -> {
            ArrayList<String> rangeOfValues = database.range("10000", "11000");
            for (String value : rangeOfValues) {
                assertEquals("18", value);
            }
            return true;
        });

        waitForFuture(future);

        secondsElapsed = (double)(System.nanoTime() - startNanos) / 1000000000d;

        System.out.printf("Range Query Count = 1000; Seconds elapsed=%f; Reads per second=%f\n", secondsElapsed, (1000d / secondsElapsed));

        startNanos = System.nanoTime();

        future = executorService.submit(() -> {
            Iterator<java.util.Map.Entry<String, Long>> iterator = database.iterator();

            while (iterator.hasNext()) {
                java.util.Map.Entry<String, Long> entry = iterator.next();
                assertEquals("18", database.get(entry.getKey()).orElse(NOT_FOUND));
            }

            return true;
        });

        waitForFuture(future);

        secondsElapsed = (double)(System.nanoTime() - startNanos) / 1000000000d;

        System.out.printf("Iterate Count = ~100k; Seconds elapsed=%f; Reads per second=%f\n", secondsElapsed, (100000d / secondsElapsed));
    }

    @Test
    public void databaseCompactTimeTest() {
        Storage storage = new StorageImpl("/tmp/db");
        Index index = new IndexImpl();
        Database database = new DatabaseImpl(index, storage, Level.INFO, new NoOpCompactionStrategy());
        long startNanos = System.nanoTime();

        ExecutorService executorService = Executors.newFixedThreadPool(16); /* Set equal to log. proc. count */

        startNanos = System.nanoTime();

        Future<Boolean> future = executorService.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                String key = Integer.toString(i);

                for (int j = 0; j < 10; j++) {
                    String value = Integer.toString(j * 2);
                    database.put(key, value);
                    assertEquals(value, database.get(key).orElse(NOT_FOUND));
                }
            }
            return true;
        });

        waitForFuture(future);

        double secondsElapsed = (double)(System.nanoTime() - startNanos) / 1000000000d;

        System.out.printf("Hundred Thousand Key Insert Count = 1M; Seconds elapsed=%f; Inserts per second=%f\n", secondsElapsed, (1000000d / secondsElapsed));

        startNanos = System.nanoTime();

        future = executorService.submit(() -> {
            database.compact();
            return true;
        });

        waitForFuture(future);

        secondsElapsed = (double)(System.nanoTime() - startNanos) / 1000000000d;

        System.out.printf("Compaction time=%f\n", secondsElapsed);
    }

    private void waitForFuture(Future<Boolean> future) {
        while(!future.isDone()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
