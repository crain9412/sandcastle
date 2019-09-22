package com.jwcrain.sandcastle;

import com.jwcrain.sandcastle.crainhashmap.Map;
import org.junit.Test;

import java.math.BigInteger;

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

}
