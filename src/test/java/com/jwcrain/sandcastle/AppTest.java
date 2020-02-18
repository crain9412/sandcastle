package com.jwcrain.sandcastle;

import com.jwcrain.sandcastle.crainhashmap.Map;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.UUID;

import static org.junit.Assert.*;

public class AppTest {
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

//    @Test
//    public void testHAMT() {
//        HashArrayMappedTrie<String, String> hamt = new HashArrayMappedTrie<>();
//        int valuesToPutAndGet = 32;
//        ArrayList<UUID> usedUUIDs = new ArrayList<>(valuesToPutAndGet);
//
//        for (int i = 0; i < valuesToPutAndGet; i++) {
//            UUID uuid = UUID.randomUUID();
//            usedUUIDs.add(uuid);
//            hamt.put(String.valueOf(i), "value" + i);
//        }
//
//        System.out.println(hamt.toString());
//
//        for (int i = 0; i < valuesToPutAndGet; i++) {
//            System.out.printf("Got %s, expected %s\n", hamt.get(usedUUIDs.get(i).toString()), "value" + i);
//            assert(hamt.get(usedUUIDs.get(i).toString()) != null);
//            assert(hamt.get(usedUUIDs.get(i).toString()).equals("value" + i));
//        }
//    }

    @Test
    public void arrayPlayground() {
        int[] arr = new int[32];

        Arrays.fill(arr, 0);

        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }

        assert(arr[23] == 23);
    }

    @Test
    public void matrixPlayground() {
        int[][] arr = new int[32][];

        for (int i = 0; i < 32; i++) {
            arr[i] = new int[32];
            Arrays.fill(arr[i], 0);
        }

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                arr[i][j] = i * j;
            }
        }

        assert(arr[3][5] == 15);
    }

    @Test
    public void bitPlayground() {
        /* 1111 */
        int bits = 8;

        assert(unsetBit(bits, 3) == 4);
        assert(unsetBit(bits, 0) == 3);
        assert(getBit(bits, 2));

        /* 0 */
        int bits2 = 0;

        assert(setBit(bits2, 4) == 8);
    }

    private int setBit(int num, int i) {
        System.out.printf("Input number %s, input index %s, output number %s\n", Integer.toBinaryString(num), i, Integer.toBinaryString(num | (1 << i)));
        return num | (1 << i);
    }

    private boolean getBit(int num, int i) {
        return (num & (1 << i)) != 0;
    }

    private int unsetBit(int num, int i) {
        int mask = ~(num & (1 << i));
        System.out.printf("Input number %s, input index %s, mask %s, output number %s\n", Integer.toBinaryString(num), i, Integer.toBinaryString(mask), Integer.toBinaryString(num | (1 << i)));
        return num & mask;
    }
}
